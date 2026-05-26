using System.Text.Json.Serialization;

namespace PCRemoteListener.Models;

public class RemoteCommand
{
    [JsonPropertyName("type")]
    public string Type { get; set; } = string.Empty;

    [JsonPropertyName("value")]
    public int? Value { get; set; }

    [JsonPropertyName("requestId")]
    public string? RequestId { get; set; }
}
