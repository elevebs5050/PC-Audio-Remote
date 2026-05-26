PC Remote Listener — Quick Start Guide
========================================
Author: Sebai Mohamed Safa
Version: 1.0

WHAT IS THIS?
-------------
PC Remote Listener is a Windows application that allows you to control your
computer's volume and media playback from an Android device over your local
network.

HOW TO USE
----------
1. After installation, launch PC Remote Listener from the Start Menu or
   desktop shortcut.

2. The application will appear in your system tray. Click to open the
   control window.

3. Note the IP addresses and port (19090) displayed in the status panel.
   You will need these to connect from your Android device.

4. Install the companion Android app (PC Remote App) on your Android device.

5. In the Android app:
   a. Enter your PC's IP address and port 19090, OR
   b. Tap "Discover PCs on Network" for auto-discovery
   c. Ensure TLS is enabled on both sides
   d. Tap "Connect"

6. Once connected, you can:
   - Adjust system volume (0-100%)
   - Play/Pause media
   - Next/Previous track

TLS SECURITY
------------
TLS (Transport Layer Security) is enabled by default. This encrypts all
communication between your Android device and PC. The server generates a
self-signed certificate on first run.

To copy the certificate to share with your Android app:
- Click "Copy Certificate" in the app footer
- The PEM-encoded certificate will be copied to your clipboard

IMPORTANT NOTES
---------------
- Both devices must be on the same local network
- Windows Firewall may prompt for network access — allow it
- The server runs in the system tray; closing the window minimizes it
- Use "Exit" from the tray icon menu to fully quit
- Your network may be blocked by firewall if you haven't granted the right access

TROUBLESHOOTING
---------------
Q: Can't connect from Android
A: Verify both devices are on the same network, check firewall settings,
   and ensure TLS mode matches on both sides.

Q: Volume controls not working
A: Run the application as administrator if you encounter issues with
   volume control on certain Windows configurations.

Q: Auto-discovery not finding the server
A: Enter the IP address manually. Some routers block UDP multicast traffic.

DISCLAIMER
----------
This is a "vibe coded" project. The author is not responsible for any
damages or issues arising from its use. See the EULA for details.
