using System.IO;
using System.Windows;
using PCRemoteListener.Services;

namespace PCRemoteListener;

public partial class App : Application
{
    private TcpServerService? _tcpServer;
    private DiscoveryService? _discovery;

    protected override void OnStartup(StartupEventArgs e)
    {
        try
        {
            base.OnStartup(e);

            var certService = new CertificateService();
            var cert = certService.LoadOrCreate();

            _tcpServer = new TcpServerService(cert, 19090);
            var fingerprint = cert.GetCertHashString();
            _discovery = new DiscoveryService(_tcpServer.Port, fingerprint);

            _tcpServer.Start();
            _discovery.Start();

            var mainWindow = new MainWindow(_tcpServer, certService);
            mainWindow.Show();
        }
        catch (Exception ex)
        {
            MessageBox.Show($"Startup error:\n{ex.Message}\n\n{ex.StackTrace}",
                "PC Remote Listener",
                MessageBoxButton.OK,
                MessageBoxImage.Error);
            Shutdown();
        }
    }

    protected override void OnExit(ExitEventArgs e)
    {
        _discovery?.Dispose();
        _tcpServer?.Dispose();
        base.OnExit(e);
    }
}
