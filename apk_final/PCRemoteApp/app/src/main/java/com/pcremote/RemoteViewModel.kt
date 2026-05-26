package com.pcremote

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.pcremote.model.DiscoveredServer
import com.pcremote.service.DiscoveryClient
import com.pcremote.service.TcpClient
import kotlinx.coroutines.launch

class RemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("pc_remote_prefs", android.content.Context.MODE_PRIVATE)
    private var tcpClient: TcpClient? = null
    private val discoveryClient = DiscoveryClient(application)

    var hasAcceptedEula by mutableStateOf(prefs.getBoolean("eula_accepted", false))
        private set
    var isConnected by mutableStateOf(false)
        private set
    var serverName by mutableStateOf<String?>(null)
        private set
    var volume by mutableStateOf(50)
        private set
    var isMuted by mutableStateOf(false)
        private set
    var discoveredServers by mutableStateOf(listOf<DiscoveredServer>())
        private set
    var isDiscovering by mutableStateOf(false)
        private set

    fun acceptEula() {
        prefs.edit().putBoolean("eula_accepted", true).apply()
        hasAcceptedEula = true
    }

    fun discover() {
        viewModelScope.launch {
            isDiscovering = true
            try {
                discoveredServers = discoveryClient.discover()
            } catch (e: Exception) {
                discoveredServers = emptyList()
            }
            isDiscovering = false
        }
    }

    fun connect(ip: String, port: Int, useTls: Boolean) {
        viewModelScope.launch {
            disconnect()
            val client = TcpClient(ip, port)
            if (client.connect(useTls = useTls)) {
                tcpClient = client
                isConnected = true
                serverName = "$ip:$port"
                volume = client.getVolume() ?: 50
            }
        }
    }

    fun disconnect() {
        tcpClient?.disconnect()
        tcpClient = null
        isConnected = false
        serverName = null
    }

    fun updateVolume(level: Int) {
        viewModelScope.launch {
            if (tcpClient?.setVolume(level) == true) {
                volume = level
            }
        }
    }

    fun toggleMute() {
        viewModelScope.launch {
            if (tcpClient?.toggleMute() == true) {
                isMuted = !isMuted
            }
        }
    }

    fun mediaPlayPause() {
        viewModelScope.launch { tcpClient?.mediaPlayPause() }
    }

    fun mediaNext() {
        viewModelScope.launch { tcpClient?.mediaNext() }
    }

    fun mediaPrevious() {
        viewModelScope.launch { tcpClient?.mediaPrevious() }
    }

    fun refreshStatus() {
        viewModelScope.launch {
            volume = tcpClient?.getVolume() ?: volume
        }
    }

    override fun onCleared() {
        super.onCleared()
        tcpClient?.disconnect()
    }
}
