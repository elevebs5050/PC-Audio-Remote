package com.pcremote.model

data class RemoteCommand(
    val type: String,
    val value: Int? = null,
    val requestId: String? = null
)

data class CommandResponse(
    val type: String,
    val success: Boolean,
    val value: Int? = null,
    val message: String? = null,
    val requestId: String? = null
)
