package com.example.smartcut.database

import com.example.smartcut.database.tables.CutSettingsTable
import com.example.smartcut.database.tables.IngredientsTable
import com.example.smartcut.database.tables.RecipesTable
import com.example.smartcut.database.tables.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/smartcut_app"
            driverClassName = "org.postgresql.Driver"
            username = "postgres"
            password = "postgres"
            maximumPoolSize = 10
        }

        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(
                UsersTable,
                RecipesTable,
                IngredientsTable,
                CutSettingsTable
            )
        }
    }
}