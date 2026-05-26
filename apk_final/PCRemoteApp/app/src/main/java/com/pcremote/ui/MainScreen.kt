package com.pcremote.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pcremote.model.DiscoveredServer

/**
 * Main UI for the PC Remote application.
 * Handles the display of connection status, discovery, and hardware controls.
 *
 * @param isConnected Whether the app is currently connected to a PC.
 * @param serverName The name of the connected PC (if any).
 * @param brightness Current brightness level (0-100).
 * @param volume Current volume level (0-100).
 * @param isMuted Whether the PC audio is muted.
 * @param discoveredServers List of PCs found on the network.
 * @param isDiscovering Whether a network scan is in progress.
 * @param onBrightnessChange Callback when brightness is adjusted.
 * @param onVolumeChange Callback when volume is adjusted.
 * @param onMuteToggle Callback to toggle mute state.
 * @param onPlayPause Callback for media Play/Pause.
 * @param onStop Callback for media Stop.
 * @param onNext Callback for media Next track.
 * @param onPrevious Callback for media Previous track.
 * @param onDiscover Callback to start PC discovery.
 * @param onConnect Callback to connect to a specific PC.
 * @param onDisconnect Callback to disconnect from the current PC.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isConnected: Boolean,
    serverName: String?,
    volume: Int,
    isMuted: Boolean,
    discoveredServers: List<DiscoveredServer>,
    isDiscovering: Boolean,
    onVolumeChange: (Int) -> Unit,
    onMuteToggle: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onDiscover: () -> Unit,
    onConnect: (String, Int, Boolean) -> Unit,
    onDisconnect: () -> Unit,
    onRefreshStatus: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "PC Remote",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        if (serverName != null) {
                            Text(
                                serverName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                actions = {
                    ConnectionIndicator(isConnected)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            
            ConnectionSection(
                isConnected = isConnected,
                discoveredServers = discoveredServers,
                isDiscovering = isDiscovering,
                onDiscover = onDiscover,
                onConnect = onConnect,
                onDisconnect = onDisconnect
            )

            if (isConnected) {
                ControlSection(
                    title = "Audio",
                    icon = Icons.Rounded.VolumeUp
                ) {
                    VolumeSlider(
                        volume = volume,
                        isMuted = isMuted,
                        onVolumeChange = onVolumeChange,
                        onMuteToggle = onMuteToggle
                    )
                }

                ControlSection(
                    title = "Media",
                    icon = Icons.Rounded.PlayCircle
                ) {
                    MediaControls(
                        onPlayPause = onPlayPause,
                        onNext = onNext,
                        onPrevious = onPrevious
                    )
                }
            } else {
                EmptyStatePlaceholder()
            }
            
            FooterSignature()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ConnectionIndicator(isConnected: Boolean) {
    Surface(
        modifier = Modifier.padding(end = 16.dp),
        color = if (isConnected) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isConnected) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.5f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD))
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (isConnected) "ONLINE" else "OFFLINE",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ControlSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
        content()
    }
}

@Composable
private fun ConnectionSection(
    isConnected: Boolean,
    discoveredServers: List<DiscoveredServer>,
    isDiscovering: Boolean,
    onDiscover: () -> Unit,
    onConnect: (String, Int, Boolean) -> Unit,
    onDisconnect: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            if (!isConnected) {
                var manualIp by remember { mutableStateOf("") }
                var manualPort by remember { mutableStateOf("19090") }
                var useTls by remember { mutableStateOf(true) }

                Text(
                    "Connection",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = manualIp,
                    onValueChange = { manualIp = it },
                    label = { Text("PC IP Address") },
                    placeholder = { Text("192.168.1.XX") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Rounded.Dns, null) }
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = manualPort,
                        onValueChange = { manualPort = it },
                        label = { Text("Port") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(100.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Secure (TLS)", style = MaterialTheme.typography.bodySmall)
                            Switch(
                                checked = useTls,
                                onCheckedChange = { useTls = it },
                                thumbContent = {
                                    if (useTls) Icon(Icons.Rounded.Lock, null, Modifier.size(12.dp))
                                }
                            )
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        val ip = manualIp.trim()
                        val port = manualPort.toIntOrNull() ?: 19090
                        if (ip.isNotEmpty()) onConnect(ip, port, useTls)
                    },
                    enabled = manualIp.trim().isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Icon(Icons.Rounded.Link, null)
                    Spacer(Modifier.width(8.dp))
                    Text("CONNECT TO PC", fontWeight = FontWeight.Bold)
                }

                Divider(
                    modifier = Modifier.padding(vertical = 20.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                )

                OutlinedButton(
                    onClick = onDiscover,
                    enabled = !isDiscovering,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isDiscovering) {
                        Text("Searching...")
                    } else {
                        Icon(Icons.Rounded.Search, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Auto-Discover PCs")
                    }
                }

                if (discoveredServers.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    discoveredServers.forEach { server ->
                        ServerItem(server = server) {
                            onConnect(server.ipAddress, server.port, true)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Computer,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Connected to", style = MaterialTheme.typography.labelSmall)
                        Text("Your Workstation", fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onDisconnect,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Icons.Rounded.LinkOff, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("EXIT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerItem(server: DiscoveredServer, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Computer, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(server.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(
                    server.ipAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Rounded.ChevronRight, null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun VolumeSlider(
    volume: Int,
    isMuted: Boolean,
    onVolumeChange: (Int) -> Unit,
    onMuteToggle: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Master Volume", fontWeight = FontWeight.Medium)
                    if (isMuted) {
                        Text("System Muted", color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (isMuted) "0%" else "$volume%",
                        color = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.width(16.dp))
                    FilledIconButton(
                        onClick = onMuteToggle,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            if (isMuted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
                            "Mute"
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            var currentVal by remember(volume) { mutableFloatStateOf(volume.toFloat()) }
            Slider(
                value = currentVal,
                onValueChange = { currentVal = it },
                onValueChangeFinished = { onVolumeChange(currentVal.toInt()) },
                valueRange = 0f..100f,
                steps = 99,
                enabled = !isMuted
            )
        }
    }
}

@Composable
private fun MediaControls(
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MediaButton(Icons.Rounded.SkipPrevious, "Prev", onPrevious)
            
            LargeMediaButton(Icons.Rounded.PlayArrow, onPlayPause)
            
            MediaButton(Icons.Rounded.SkipNext, "Next", onNext)
        }
    }
}

@Composable
private fun MediaButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(52.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Icon(icon, label, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun LargeMediaButton(icon: ImageVector, onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        shape = RoundedCornerShape(20.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(icon, "Play/Pause", modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun EmptyStatePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = CircleShape,
            modifier = Modifier.size(120.dp)
        ) {
            Icon(
                Icons.Rounded.CloudOff,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                modifier = Modifier.padding(32.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            "No Connection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Text(
            "Connect to a PC to see controls",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun FooterSignature() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Divider(
            modifier = Modifier.padding(horizontal = 40.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Created by",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
        )
        Text(
            "Sebai Mohamed Safa",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    }
}
