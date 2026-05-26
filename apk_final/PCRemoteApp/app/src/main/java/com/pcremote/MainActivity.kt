package com.pcremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.pcremote.ui.EulaScreen
import com.pcremote.ui.MainScreen
import com.pcremote.ui.PCRemoteTheme

class MainActivity : ComponentActivity() {

    private val viewModel: RemoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PCRemoteTheme {
                if (!viewModel.hasAcceptedEula) {
                    EulaScreen(onAgree = { viewModel.acceptEula() })
                } else {
                    MainScreen(
                        isConnected = viewModel.isConnected,
                        serverName = viewModel.serverName,
                        volume = viewModel.volume,
                        isMuted = viewModel.isMuted,
                        discoveredServers = viewModel.discoveredServers,
                        isDiscovering = viewModel.isDiscovering,
                        onVolumeChange = { viewModel.updateVolume(it) },
                        onMuteToggle = { viewModel.toggleMute() },
                        onPlayPause = { viewModel.mediaPlayPause() },
                        onNext = { viewModel.mediaNext() },
                        onPrevious = { viewModel.mediaPrevious() },
                        onDiscover = { viewModel.discover() },
                        onConnect = { ip, port, useTls -> viewModel.connect(ip, port, useTls) },
                        onDisconnect = { viewModel.disconnect() },
                        onRefreshStatus = { viewModel.refreshStatus() }
                    )
                }
            }
        }
    }
}
