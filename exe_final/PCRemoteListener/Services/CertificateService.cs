using System.IO;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;

namespace PCRemoteListener.Services;

public class CertificateService
{
    private readonly string _pfxPath;
    private readonly string _cerPath;

    public CertificateService()
    {
        var appDir = Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
            "PCRemoteListener");
        Directory.CreateDirectory(appDir);
        _pfxPath = Path.Combine(appDir, "pc_remote.pfx");
        _cerPath = Path.Combine(appDir, "pc_remote.cer");
    }

    public X509Certificate2 LoadOrCreate()
    {
        if (File.Exists(_pfxPath))
        {
            return new X509Certificate2(_pfxPath, "",
                X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.PersistKeySet | X509KeyStorageFlags.Exportable);
        }

        using var rsa = RSA.Create(4096);
        var request = new CertificateRequest(
            "CN=PCRemoteListener",
            rsa,
            HashAlgorithmName.SHA256,
            RSASignaturePadding.Pkcs1);

        request.CertificateExtensions.Add(new X509BasicConstraintsExtension(false, false, 0, true));
        request.CertificateExtensions.Add(new X509KeyUsageExtension(
            X509KeyUsageFlags.KeyEncipherment | X509KeyUsageFlags.DigitalSignature, true));
        request.CertificateExtensions.Add(new X509EnhancedKeyUsageExtension(
            new OidCollection { new Oid("1.3.6.1.5.5.7.3.1") }, true));

        var san = new SubjectAlternativeNameBuilder();
        san.AddDnsName(Environment.MachineName);
        san.AddIpAddress(System.Net.IPAddress.Loopback);
        request.CertificateExtensions.Add(san.Build());

        var cert = request.CreateSelfSigned(
            DateTimeOffset.UtcNow.AddDays(-1),
            DateTimeOffset.UtcNow.AddYears(10));

        File.WriteAllBytes(_pfxPath, cert.Export(X509ContentType.Pfx, ""));
        File.WriteAllBytes(_cerPath, cert.Export(X509ContentType.Cert));

        return new X509Certificate2(_pfxPath, "",
            X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.PersistKeySet | X509KeyStorageFlags.Exportable);
    }

    public string GetCerPath() => _cerPath;
}
