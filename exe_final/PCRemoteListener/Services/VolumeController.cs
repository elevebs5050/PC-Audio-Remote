using System.Runtime.InteropServices;
using NAudio.CoreAudioApi;

namespace PCRemoteListener.Services;

public class VolumeController
{
    private MMDevice? _device;

    private MMDevice GetDevice()
    {
        if (_device != null) return _device;
        var enumerator = new MMDeviceEnumerator();
        _device = enumerator.GetDefaultAudioEndpoint(DataFlow.Render, Role.Console);
        return _device;
    }

    public float GetVolume()
    {
        return GetDevice().AudioEndpointVolume.MasterVolumeLevelScalar * 100;
    }

    public void SetVolume(int level)
    {
        var scalar = Math.Clamp(level / 100f, 0f, 1f);
        GetDevice().AudioEndpointVolume.MasterVolumeLevelScalar = scalar;
    }

    public bool GetMute()
    {
        return GetDevice().AudioEndpointVolume.Mute;
    }

    public void SetMute(bool mute)
    {
        GetDevice().AudioEndpointVolume.Mute = mute;
    }

    public void ToggleMute()
    {
        var dev = GetDevice();
        dev.AudioEndpointVolume.Mute = !dev.AudioEndpointVolume.Mute;
    }
}
