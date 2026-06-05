package com.example.smartcut.models.domain

data class CutSettings(
    val userId: Int,
    val mode: String,
    val size: Int,
    val speed: Float
)