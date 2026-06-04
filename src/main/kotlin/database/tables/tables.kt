package com.example.smartcut.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = timestamp("created_at").default(Instant.now())
    override val primaryKey = PrimaryKey(id)
}

object RecipesTable : Table("recipes") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val cookingTime = varchar("cooking_time", 100).nullable()
    val imageUrl = varchar("image_url", 500).nullable()
    val userId = integer("user_id").references(UsersTable.id)
    val createdAt = timestamp("created_at").default(Instant.now())
    override val primaryKey = PrimaryKey(id)
}

object IngredientsTable : Table("ingredients") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val amount = varchar("amount", 100).nullable()
    val recipeId = integer("recipe_id").references(RecipesTable.id)
    override val primaryKey = PrimaryKey(id)
}

object CutSettingsTable : Table("cut_settings") {
    val id = integer("id").autoIncrement()
    val mode = varchar("mode", 50)
    val size = integer("size")
    val speed = float("speed")
    val userId = integer("user_id").references(UsersTable.id)
    val updatedAt = timestamp("updated_at").default(Instant.now())
    override val primaryKey = PrimaryKey(id)
}
obje