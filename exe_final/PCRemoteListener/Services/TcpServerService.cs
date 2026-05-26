using System.IO;
using System.Net;
using System.Net.Security;
using System.Net.Sockets;
using System.Security.Cryptography.X509Certificates;
using System.Text.Json;
using PCRemoteListener.Models;

namespace PCRemoteListener.Services;

public class TcpServerService : IDisposable
{
    private readonly X509Certificate2 _certificate;
    private readonly VolumeController _volume;
    private readonly MediaKeyController _mediaKeys;
    private readonly int _port;
    private TcpListener? _listener;
    private CancellationTokenSource? _cts;

    public int Port => _port;
    public bool RequireTls { get; set; } = true;
    public event Action<string>? OnLog;

    public TcpServerService(X509Certificate2 certificate, int port = 19090)
    {
        _certificate = certificate;
        _volume = new VolumeController();
        _mediaKeys = new MediaKeyController();
        _port = port;
    }

    public void Start()
    {
        Stop();
        _cts = new CancellationTokenSource();
        _listener = new TcpListener(IPAddress.Any, _port);
        _listener.Start();
        OnLog?.Invoke($"Server listening on port {_port} (TLS: {(RequireTls ? "ON" : "OFF")})");
        _ = AcceptClientsAsync(_cts.Token);
    }

    public void Stop()
    {
        _cts?.Cancel();
        _listener?.Stop();
        _listener = null;
        _cts?.Dispose();
        _cts = null;
    }

    private async Task AcceptClientsAsync(CancellationToken ct)
    {
        while (!ct.IsCancellationRequested)
        {
            try
            {
                var client = await _listener!.AcceptTcpClientAsync().WaitAsync(ct);
                OnLog?.Invoke($"Client connected: {client.Client.RemoteEndPoint}");
                _ = HandleClientAsync(client, ct);
            }
            catch (OperationCanceledException) { break; }
            catch { }
        }
    }

    private async Task HandleClientAsync(TcpClient client, CancellationToken ct)
    {
        try
        {
            using (client)
            {
                Stream stream = client.GetStream();

                if (RequireTls)
                {
                    var sslStream = new SslStream(stream, false);
                    await sslStream.AuthenticateAsServerAsync(_certificate, false,
                        System.Security.Authentication.SslProtocols.Tls12 | System.Security.Authentication.SslProtocols.Tls13,
                        false).WaitAsync(ct);
                    stream = sslStream;
                }

                using var reader = new StreamReader(stream);
                await using var writer = new StreamWriter(stream) { AutoFlush = true, NewLine = "\n" };

                while (!ct.IsCancellationRequested)
                {
                    var line = await reader.ReadLineAsync(ct);
                    if (line == null) break;

                    if (string.IsNullOrWhiteSpace(line)) continue;

                    CommandResponse? response = null;
                    try
                    {
                        var cmd = JsonSerializer.Deserialize<RemoteCommand>(line);
                        if (cmd != null)
                        {
                            response = ExecuteCommand(cmd);
                            OnLog?.Invoke($"Command: {cmd.Type}");
                        }
                    }
                    catch (JsonException ex)
                    {
                        response = new CommandResponse
                        {
                            Type = "Error",
                            Success = false,
                            Message = $"Invalid JSON: {ex.Message}"
                        };
                    }

                    if (response != null)
                    {
                        await writer.WriteLineAsync(JsonSerializer.Serialize(response));
                    }
                }
            }
        }
        catch (Exception ex)
        {
            OnLog?.Invoke($"Client error: {ex.Message}");
        }
    }

    private CommandResponse ExecuteCommand(RemoteCommand cmd)
    {
        try
        {
            return cmd.Type switch
            {
                "VolumeGet" => new CommandResponse
                {
                    Type = cmd.Type,
                    Success = true,
                    Value = (int)_volume.GetVolume(),
                    RequestId = cmd.RequestId
                },

                "VolumeSet" => Execute(() => _volume.SetVolume(cmd.Value ?? 50), cmd),

                "VolumeMute" => Execute(_volume.ToggleMute, cmd),

                "MediaPlayPause" => Execute(_mediaKeys.SendPlayPause, cmd),
                "MediaStop" => Execute(_mediaKeys.SendStop, cmd),
                "MediaNext" => Execute(_mediaKeys.SendNext, cmd),
                "MediaPrevious" => Execute(_mediaKeys.SendPrevious, cmd),

                _ => new CommandResponse
                {
                    Type = cmd.Type,
                    Success = false,
                    Message = $"Unknown command: {cmd.Type}",
                    RequestId = cmd.RequestId
                }
            };
        }
        catch (Exception ex)
        {
            return new CommandResponse
            {
                Type = cmd.Type,
                Success = false,
                Message = ex.Message,
                RequestId = cmd.RequestId
            };
        }
    }

    private static CommandResponse Execute(Action action, RemoteCommand cmd)
    {
        action();
        return new CommandResponse { Type = cmd.Type, Success = true, RequestId = cmd.RequestId };
    }

    public void Dispose()
    {
        Stop();
    }
}
