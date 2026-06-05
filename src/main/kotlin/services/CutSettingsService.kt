package com.example.smartcut.services

import com.example.smartcut.models.dto.CutSettingsRequest
import com.example.smartcut.models.dto.CutSettingsResponse
import com.example.smartcut.repositories.CutSettingsRepository

class CutSettingsService(private val repository: CutSettingsRepository) {

    suspend fun get(userId: Int): CutSettingsResponse? {
        val settings = repository.getByUserId(userId) ?: return null
        return CutSettingsResponse(
            userId = settings.userId,
            mode = settings.mode,
            size = settings.size,
            speed = settings.speed
        )
    }

    suspend fun save(userId: Int, request: CutSettingsRequest): CutSettingsResponse {
        val settings = repository.save(userId, request.mode, request.size, request.speed)
        return CutSettingsResponse(
            userId = settings.userId,
            mode = settings.mode,
            size = settings.size,
            speed = settings.speed
        )
    }
}