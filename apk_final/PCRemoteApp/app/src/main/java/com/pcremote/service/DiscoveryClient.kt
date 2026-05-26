package com.pcremote.service

import android.content.Context
import android.net.wifi.WifiManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.pcremote.model.DiscoveredServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketTimeoutException

class DiscoveryClient(private val context: Context) {

    private val gson = Gson()
    private val multicastGroup = InetAddress.getByName("239.255.255.250")
    private val discoveryPort = 4096

    suspend fun discover(timeout: Long = 3000): List<DiscoveredServer> = withContext(Dispatchers.IO) {
        val servers = mutableListOf<DiscoveredServer>()
        var multicastLock: WifiManager.MulticastLock? = null
        var socket: DatagramSocket? = null

        try {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            if (wifiManager != null) {
                multicastLock = wifiManager.createMulticastLock("PCRemoteDiscovery")
                multicastLock?.setReferenceCounted(false)
                multicastLock?.acquire()
            }

            socket = DatagramSocket()
            socket.broadcast = true
            socket.soTimeout = timeout.toInt()

            val discoverMsg = "PC_REMOTE_DISCOVER"
            val sendPacket = DatagramPacket(
                discoverMsg.toByteArray(),
                discoverMsg.length,
                multicastGroup,
                discoveryPort
            )
            socket.send(sendPacket)

            val buffer = ByteArray(1024)
            val endTime = System.currentTimeMillis() + timeout

            while (System.currentTimeMillis() < endTime) {
                try {
                    val receivePacket = DatagramPacket(buffer, buffer.size)
                    socket.receive(receivePacket)
                    val json = String(receivePacket.data, 0, receivePacket.length)
                    val obj = gson.fromJson(json, JsonObject::class.java)
                    val ip = receivePacket.address.hostAddress
                    if (ip != null) {
                        servers.add(
                            DiscoveredServer(
                                name = obj.get("name")?.asString ?: "Unknown",
                                ipAddress = ip,
                                port = obj.get("port")?.asInt ?: 19090,
                                fingerprint = obj.get("fingerprint")?.asString ?: ""
                            )
                        )
                    }
                } catch (_: SocketTimeoutException) {
                    break
                }
            }
        } catch (_: Exception) {
        } finally {
            try { multicastLock?.release() } catch (_: Exception) { }
            try { socket?.close() } catch (_: Exception) { }
        }

        servers
    }
}
