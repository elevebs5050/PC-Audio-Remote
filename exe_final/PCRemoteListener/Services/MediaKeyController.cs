using System.Runtime.InteropServices;

namespace PCRemoteListener.Services;

public class MediaKeyController
{
    private const byte VK_MEDIA_PLAY_PAUSE = 0xB3;
    private const byte VK_MEDIA_STOP = 0xB2;
    private const byte VK_MEDIA_NEXT_TRACK = 0xB0;
    private const byte VK_MEDIA_PREV_TRACK = 0xB1;

    public void SendPlayPause() => SimulateKey(VK_MEDIA_PLAY_PAUSE);
    public void SendStop() => SimulateKey(VK_MEDIA_STOP);
    public void SendNext() => SimulateKey(VK_MEDIA_NEXT_TRACK);
    public void SendPrevious() => SimulateKey(VK_MEDIA_PREV_TRACK);

    private static void SimulateKey(byte vkCode)
    {
        var inputs = new INPUT[2];

        // Key down
        inputs[0] = new INPUT
        {
            type = 1, // INPUT_KEYBOARD
            u = new InputUnion
            {
                ki = new KEYBDINPUT
                {
                    wVk = vkCode,
                    wScan = 0,
                    dwFlags = 0,
                    time = 0,
                    dwExtraInfo = IntPtr.Zero
                }
            }
        };

        // Key up
        inputs[1] = new INPUT
        {
            type = 1,
            u = new InputUnion
            {
                ki = new KEYBDINPUT
                {
                    wVk = vkCode,
                    wScan = 0,
                    dwFlags = 2, // KEYEVENTF_KEYUP
                    time = 0,
                    dwExtraInfo = IntPtr.Zero
                }
            }
        };

        _ = SendInput((uint)inputs.Length, inputs, Marshal.SizeOf<INPUT>());
    }

    [DllImport("user32.dll", SetLastError = true)]
    private static extern uint SendInput(uint nInputs, INPUT[] pInputs, int cbSize);

    [StructLayout(LayoutKind.Sequential)]
    private struct INPUT
    {
        public uint type;
        public InputUnion u;
    }

    [StructLayout(LayoutKind.Explicit)]
    private struct InputUnion
    {
        [FieldOffset(0)] public MOUSEINPUT mi;
        [FieldOffset(0)] public KEYBDINPUT ki;
        [FieldOffset(0)] public HARDWAREINPUT hi;
    }

    [StructLayout(LayoutKind.Sequential)]
    private struct KEYBDINPUT
    {
        public ushort wVk;
        public ushort wScan;
        public uint dwFlags;
        public uint time;
        public IntPtr dwExtraInfo;
    }

    [StructLayout(LayoutKind.Sequential)]
    private struct MOUSEINPUT
    {
        public int dx;
        public int dy;
        public uint mouseData;
        public uint dwFlags;
        public uint time;
        public IntPtr dwExtraInfo;
    }

    [StructLayout(LayoutKind.Sequential)]
    private struct HARDWAREINPUT
    {
        public uint uMsg;
        public ushort wParamL;
        public ushort wParamH;
    }
}
