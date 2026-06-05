package com.example.smartcut.repositories

import com.example.smartcut.database.tables.CutSettingsTable
import com.example.smartcut.models.domain.CutSettings
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class CutSettingsRepository {

    suspend fun getByUserId(userId: Int): CutSettings? {
        return newSuspendedTransaction(Dispatchers.IO) {
            CutSettingsTable.selectAll()
                .where { CutSettingsTable.userId eq userId }
                .map { it.toCutSettings() }
                .singleOrNull()
        }
    }

    suspend fun save(userId: Int, mode: String, size: Int, speed: Float): CutSettings {
        return newSuspendedTransaction(Dispatchers.IO) {
            val existing = CutSettingsTable.selectAll()
                .where { CutSettingsTable.userId eq userId }
                .singleOrNull()

            if (existing != null) {
                CutSettingsTable.update({ CutSettingsTable.userId eq userId }) {
                    it[CutSettingsTable.mode] = mode
                    it[CutSettingsTable.size] = size
                    it[CutSettingsTable.speed] = speed
                }
            } else {
                CutSettingsTable.insert {
                    it[CutSettingsTable.userId] = userId
                    it[CutSettingsTable.mode] = mode
                    it[CutSettingsTable.size] = size
                    it[CutSettingsTable.speed] = speed
                }
            }

            CutSettings(userId, mode, size, speed)
        }
    }

    private fun ResultRow.toCutSettings() = CutSettings(
        userId = this[CutSettingsTable.userId],
        mode = this[CutSettingsTable.mode],
        size = this[CutSettingsTable.size],
        speed = this[CutSettingsTable.speed]
    )
}