package com.example.smartcut.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class CutSettingsRequest(
    val mode: String,
    val size: Int,
    val speed: Float
)

@Serializable
data class CutSettingsResponse(
    val userId: Int,
    val mode: String,
    val size: Int,
    val speed: Float
)