package com.example.smartcut.repositories

import com.example.smartcut.database.tables.UsersTable
import com.example.smartcut.models.domain.User
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserRepository {

    suspend fun create(name: String, email: String, passwordHash: String): User {
        return newSuspendedTransaction(Dispatchers.IO) {
            val id = UsersTable.insert {
                it[UsersTable.name] = name
                it[UsersTable.email] = email
                it[UsersTable.passwordHash] = passwordHash
            } get UsersTable.id
            User(id, name, email, passwordHash)
        }
    }

    suspend fun findByEmail(email: String): User? {
        return newSuspendedTransaction(Dispatchers.IO) {
            UsersTable.selectAll().where { UsersTable.email eq email }
                .map { it.toUser() }
                .singleOrNull()
        }
    }

    suspend fun findById(id: Int): User? {
        return newSuspendedTransaction(Dispatchers.IO) {
            UsersTable.selectAll().where { UsersTable.id eq id }
                .map { it.toUser() }
                .singleOrNull()
        }
    }

    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id],
        name = this[UsersTable.name],
        email = this[UsersTable.email],
        passwordHash = this[UsersTable.passwordHash]
    )
}