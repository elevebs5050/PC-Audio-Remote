package com.pcremote.service

import com.google.gson.Gson
import com.pcremote.model.CommandResponse
import com.pcremote.model.RemoteCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.*

class TcpClient(
    private val host: String,
    private val port: Int
) {
    private val gson = Gson()
    private var socket: Socket? = null
    private var reader: BufferedReader? = null
    private var writer: OutputStreamWriter? = null
    private val mutex = Mutex()

    suspend fun connect(useTls: Boolean = true, serverCertificatePem: String? = null): Boolean = withContext(Dispatchers.IO) {
        try {
            if (useTls) {
                val sslContext = createSslContext(serverCertificatePem)
                val factory = sslContext.socketFactory as SSLSocketFactory
                socket = factory.createSocket(host, port) as SSLSocket
                (socket as SSLSocket).startHandshake()
            } else {
                socket = Socket(host, port)
            }

            reader = BufferedReader(InputStreamReader(socket!!.inputStream))
            writer = OutputStreamWriter(socket!!.outputStream)

            // Verify connection works by sending a ping command
            val verify = sendCommand(RemoteCommand("BrightnessGet", requestId = "verify"))
            if (verify == null) {
                disconnect()
                return@withContext false
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun sendCommand(command: RemoteCommand): CommandResponse? = mutex.withLock {
        withContext(Dispatchers.IO) {
            try {
                val json = gson.toJson(command)
                android.util.Log.d("TcpClient", "Sending: $json")
                writer?.write(json + "\n")
                writer?.flush()
                val response = reader?.readLine()
                android.util.Log.d("TcpClient", "Received: $response")
                if (response == null) return@withContext null
                gson.fromJson(response, CommandResponse::class.java)
            } catch (e: Exception) {
                android.util.Log.e("TcpClient", "Error sending command", e)
                null
            }
        }
    }

    suspend fun getBrightness(): Int? {
        val resp = sendCommand(RemoteCommand("BrightnessGet", requestId = java.util.UUID.randomUUID().toString()))
        return if (resp?.success == true) resp.value else null
    }

    suspend fun setBrightness(level: Int): Boolean {
        val resp = sendCommand(RemoteCommand("BrightnessSet", level, java.util.UUID.randomUUID().toString()))
        return resp?.success == true
    }

    suspend fun getVolume(): Int? {
        val resp = sendCommand(RemoteCommand("VolumeGet", requestId = java.util.UUID.randomUUID().toString()))
        return if (resp?.success == true) resp.value else null
    }

    suspend fun setVolume(level: Int): Boolean {
        val resp = sendCommand(RemoteCommand("VolumeSet", level, java.util.UUID.randomUUID().toString()))
        return resp?.success == true
    }

    suspend fun toggleMute(): Boolean {
        val resp = sendCommand(RemoteCommand("VolumeMute", requestId = java.util.UUID.randomUUID().toString()))
        return resp?.success == true
    }

    suspend fun mediaPlayPause(): Boolean {
        val resp = sendCommand(RemoteCommand("MediaPlayPause", requestId = java.util.UUID.randomUUID().toString()))
        return resp?.success == true
    }

    suspend fun mediaStop(): Boolean {
        val resp = sendCommand(RemoteCommand("MediaStop", requestId = java.util.UUID.randomUUID().toString()))
        return resp?.success == true
    }

    suspend fun mediaNext(): Boolean {
        val resp = sendCommand(RemoteCommand("MediaNext", requestId = java.util.UUID.randomUUID().toString()))
        return resp?.success == true
    }

    suspend fun mediaPrevious(): Boolean {
        val resp = sendCommand(RemoteCommand("MediaPrevious", requestId = java.util.UUID.randomUUID().toString()))
        return resp?.success == true
    }

    fun disconnect() {
        try { writer?.close() } catch (_: Exception) { }
        try { reader?.close() } catch (_: Exception) { }
        try { socket?.close() } catch (_: Exception) { }
        reader = null
        writer = null
        socket = null
    }

    private fun createSslContext(serverCertificatePem: String?): SSLContext {
        val sslContext = SSLContext.getInstance("TLSv1.3")

        if (serverCertificatePem != null) {
            val cf = CertificateFactory.getInstance("X.509")
            val certBytes = serverCertificatePem
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replace("\\s".toRegex(), "")
                .let { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }
            val cert = cf.generateCertificate(certBytes.inputStream()) as X509Certificate

            val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
            trustStore.load(null, null)
            trustStore.setCertificateEntry("server", cert)

            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(trustStore)
            sslContext.init(null, tmf.trustManagers, SecureRandom())
        } else {
            sslContext.init(null, arrayOf(TrustAllManager()), SecureRandom())
        }

        return sslContext
    }

    @Suppress("CustomX509TrustManager")
    private class TrustAllManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) { }
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) { }
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }
}
