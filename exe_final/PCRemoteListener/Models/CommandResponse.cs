using System.Text.Json.Serialization;

namespace PCRemoteListener.Models;

public class CommandResponse
{
    [JsonPropertyName("type")]
    public string Type { get; set; } = string.Empty;

    [JsonPropertyName("success")]
    public bool Success { get; set; }

    [JsonPropertyName("value")]
    public int? Value { get; set; }

    [JsonPropertyName("message")]
    public string? Message { get; set; }

    [JsonPropertyName("requestId")]
    public string? RequestId { get; set; }
}
