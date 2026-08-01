package com.hermex.android.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class GitCheckoutRequest(
    val session_id: String,
    val branch: String,
)

@Serializable
data class GitDiscardRequest(
    val session_id: String,
    val path: String? = null,
)
