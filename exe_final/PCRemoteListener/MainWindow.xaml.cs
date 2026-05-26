using System.Diagnostics;
using System.IO;
using System.Text;
using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using PCRemoteListener.Services;

namespace PCRemoteListener;

public partial class MainWindow : Window
{
    private readonly TcpServerService _tcpServer;
    private readonly CertificateService _certService;
    private bool _forceClose;
    private const int MaxLogEntries = 500;
    private bool _tlsEnabled = true;

    private static readonly Brush GreenBrush = new SolidColorBrush(Color.FromRgb(0x00, 0xff, 0x88));
    private static readonly Brush BlueBrush = new SolidColorBrush(Color.FromRgb(0x00, 0xd4, 0xff));
    private static readonly Brush RedBrush = new SolidColorBrush(Color.FromRgb(0xff, 0x00, 0x33));
    private static readonly Brush GoldBrush = new SolidColorBrush(Color.FromRgb(0xff, 0x66, 0x00));
    private static readonly Brush GrayBrush = new SolidColorBrush(Color.FromRgb(0xcc, 0xcc, 0xcc));

    public MainWindow(TcpServerService tcpServer, CertificateService certService)
    {
        InitializeComponent();
        _tcpServer = tcpServer;
        _certService = certService;
        _tcpServer.OnLog += OnServerLog;

        var ips = GetLocalIPs();
        StatusSubtitle.Text = string.Join(", ", ips);

        (Resources["StatusPulse"] as System.Windows.Media.Animation.Storyboard)?.Begin(this);

        TrayIcon.IconSource = BuildIcon();

        Log("PC Remote Listener v1.0 started", GoldBrush);
        Log("Author: Sebai Mohamed Safa", GoldBrush);
        Log($"Listening on {string.Join(", ", ips)}:{tcpServer.Port}", GrayBrush);
        Log($"TLS: Enabled", GreenBrush);
    }

    private ImageSource BuildIcon()
    {
        using var bmp = new System.Drawing.Bitmap(16, 16);
        using (var g = System.Drawing.Graphics.FromImage(bmp))
        {
            g.Clear(System.Drawing.Color.DimGray);
            g.DrawString("RC",
                new System.Drawing.Font("Arial", 7, System.Drawing.FontStyle.Bold),
                System.Drawing.Brushes.White, 0, 2);
        }
        using var ms = new MemoryStream();
        bmp.Save(ms, System.Drawing.Imaging.ImageFormat.Png);
        ms.Position = 0;
        var img = new BitmapImage();
        img.BeginInit();
        img.StreamSource = ms;
        img.CacheOption = BitmapCacheOption.OnLoad;
        img.EndInit();
        return img;
    }

    private void OnServerLog(string message)
    {
        Dispatcher.Invoke(() =>
        {
            var brush = GrayBrush;
            if (message.Contains("error", StringComparison.OrdinalIgnoreCase) ||
                message.Contains("fail", StringComparison.OrdinalIgnoreCase))
                brush = RedBrush;
            else if (message.Contains("connected", StringComparison.OrdinalIgnoreCase))
                brush = GreenBrush;
            else if (message.Contains("Command:", StringComparison.OrdinalIgnoreCase))
                brush = BlueBrush;

            Log(message, brush);
        });
    }

    private void Log(string message, Brush color)
    {
        var time = DateTime.Now.ToString("HH:mm:ss");
        var entry = new LogEntry { Text = $"[{time}] {message}", Brush = color };

        LogList.Items.Add(entry);
        if (LogList.Items.Count > MaxLogEntries)
            LogList.Items.RemoveAt(0);
        if (LogList.Items.Count > 0)
            LogList.ScrollIntoView(LogList.Items[LogList.Items.Count - 1]);
    }

    private void TlsToggle_Click(object sender, RoutedEventArgs e)
    {
        _tlsEnabled = !_tlsEnabled;
        _tcpServer.RequireTls = _tlsEnabled;

        TlsStatus.Text = _tlsEnabled ? "Enabled" : "Disabled";
        TlsStatus.Foreground = _tlsEnabled ? GreenBrush : RedBrush;
        TlsToggleBtn.Content = _tlsEnabled ? "Disable" : "Enable";
        TlsWarning.Text = _tlsEnabled ? "" : "Warning: unencrypted traffic";

        _tcpServer.Start();
        Log($"TLS {(_tlsEnabled ? "enabled" : "disabled")} — server restarted", GoldBrush);
    }

    private void ClearLog_Click(object sender, RoutedEventArgs e)
    {
        LogList.Items.Clear();
    }

    private void StopServer_Click(object sender, RoutedEventArgs e)
    {
        _tcpServer.Stop();
        Log("Server stopped", RedBrush);
    }

    private void RestartServer_Click(object sender, RoutedEventArgs e)
    {
        _tcpServer.Start();
        Log("Server restarted", GoldBrush);
    }

    private async void CopyCert_Click(object sender, RoutedEventArgs e)
    {
        try
        {
            var cerPath = _certService.GetCerPath();
            var certBytes = await File.ReadAllBytesAsync(cerPath);
            var base64 = Convert.ToBase64String(certBytes);

            var sb = new StringBuilder();
            sb.AppendLine("-----BEGIN CERTIFICATE-----");
            for (int i = 0; i < base64.Length; i += 64)
                sb.AppendLine(base64.Substring(i, Math.Min(64, base64.Length - i)));
            sb.AppendLine("-----END CERTIFICATE-----");

            Clipboard.SetText(sb.ToString());
            CopyCertBtn.Content = "Copied!";
            Log("Certificate copied to clipboard", GreenBrush);
            await Task.Delay(2000);
            CopyCertBtn.Content = "Copy Certificate";
        }
        catch (Exception ex)
        {
            Log($"Error copying certificate: {ex.Message}", RedBrush);
        }
    }

    private void MinBtn_Click(object sender, RoutedEventArgs e)
    {
        WindowState = WindowState.Minimized;
    }

    private void CloseBtn_Click(object sender, RoutedEventArgs e)
    {
        _forceClose = true;
        Application.Current.Shutdown();
    }

    private void Window_MouseLeftButtonDown(object sender, System.Windows.Input.MouseButtonEventArgs e)
    {
        if (e.LeftButton == System.Windows.Input.MouseButtonState.Pressed)
            DragMove();
    }

    private void ShowWindow_Click(object? sender, RoutedEventArgs? e)
    {
        Show();
        WindowState = WindowState.Normal;
        Activate();
    }

    private void Exit_Click(object? sender, RoutedEventArgs? e)
    {
        _forceClose = true;
        Application.Current.Shutdown();
    }

    private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
    {
        if (!_forceClose)
        {
            e.Cancel = true;
            Hide();
            Log("Minimized to tray", GrayBrush);
        }
    }

    private static List<string> GetLocalIPs()
    {
        var ips = new List<string>();
        foreach (var addr in System.Net.Dns.GetHostEntry(System.Net.Dns.GetHostName()).AddressList)
        {
            if (addr.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork)
                ips.Add(addr.ToString());
        }
        return ips;
    }
}

public class LogEntry
{
    public string Text { get; set; } = "";
    public Brush Brush { get; set; } = Brushes.Gray;
}
