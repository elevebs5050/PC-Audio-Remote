package com.pcremote.model

data class DiscoveredServer(
    val name: String,
    val ipAddress: String,
    val port: Int,
    val fingerprint: String
)
