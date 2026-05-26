using System.Net;
using System.Net.Sockets;
using System.Text;

namespace PCRemoteListener.Services;

public class DiscoveryService : IDisposable
{
    private readonly int _serverPort;
    private readonly string _serverName;
    private readonly string _certFingerprint;
    private UdpClient? _udpClient;
    private CancellationTokenSource? _cts;
    private const string DiscoveryMulticastGroup = "239.255.255.250";
    private const int DiscoveryPort = 4096;

    public DiscoveryService(int serverPort, string certFingerprint)
    {
        _serverPort = serverPort;
        _serverName = Environment.MachineName;
        _certFingerprint = certFingerprint;
    }

    public void Start()
    {
        _cts = new CancellationTokenSource();
        _udpClient = new UdpClient();
        _udpClient.Client.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReuseAddress, true);
        _udpClient.Client.Bind(new IPEndPoint(IPAddress.Any, DiscoveryPort));
        _udpClient.JoinMulticastGroup(IPAddress.Parse(DiscoveryMulticastGroup));

        _ = ListenAsync(_cts.Token);
    }

    private async Task ListenAsync(CancellationToken ct)
    {
        while (!ct.IsCancellationRequested)
        {
            try
            {
                var result = await _udpClient!.ReceiveAsync().WaitAsync(ct);
                var message = Encoding.UTF8.GetString(result.Buffer);

                if (message == "PC_REMOTE_DISCOVER")
                {
                    var response = $"{{\"name\":\"{_serverName}\",\"port\":{_serverPort},\"fingerprint\":\"{_certFingerprint}\"}}";
                    var data = Encoding.UTF8.GetBytes(response);
                    await _udpClient.SendAsync(data, data.Length, result.RemoteEndPoint)
                        .WaitAsync(ct);
                }
            }
            catch (OperationCanceledException) { break; }
            catch { }
        }
    }

    public void Dispose()
    {
        _cts?.Cancel();
        _udpClient?.DropMulticastGroup(IPAddress.Parse(DiscoveryMulticastGroup));
        _udpClient?.Close();
        _cts?.Dispose();
    }
}
