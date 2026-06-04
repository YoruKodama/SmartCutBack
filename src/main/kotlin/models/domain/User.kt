package com.example.smartcut.models.domain

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val passwordHash: String
)