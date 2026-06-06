package com.example.smartcut.database

import com.example.smartcut.database.tables.IngredientsTable
import com.example.smartcut.database.tables.RecipesTable
import com.example.smartcut.database.tables.UsersTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DataSeeder {

    private data class SeedIngredient(val name: String, val amount: String, val cuttable: Boolean)

    private data class SeedRecipe(
        val name: String,
        val cookingTime: String,
        val image: String,
        val ingredients: List<SeedIngredient>
    )

    private val recipes = listOf(
        SeedRecipe(
            name = "Фруктовая нарезка",
            cookingTime = "10 мин",
            image = "fruit_platter.jpg",
            ingredients = listOf(
                SeedIngredient("Яблоко", "2 шт", true),
                SeedIngredient("Киви", "2 шт", true),
                SeedIngredient("Банан", "1 шт", true),
                SeedIngredient("Апельсин", "1 шт", true),
                SeedIngredient("Виноград", "100 г", false)
            )
        ),
        SeedRecipe(
            name = "Капустный салат",
            cookingTime = "15 мин",
            image = "coleslaw.jpg",
            ingredients = listOf(
                SeedIngredient("Капуста белокочанная", "300 г", true),
                SeedIngredient("Морковь", "1 шт", true),
                SeedIngredient("Лук репчатый", "1 шт", true),
                SeedIngredient("Соль", "по вкусу", false),
                SeedIngredient("Растительное масло", "2 ст.л", false)
            )
        ),
        SeedRecipe(
            name = "Греческий салат",
            cookingTime = "15 мин",
            image = "greek_salad.jpg",
            ingredients = listOf(
                SeedIngredient("Огурец", "2 шт", true),
                SeedIngredient("Помидор", "3 шт", true),
                SeedIngredient("Болгарский перец", "1 шт", true),
                SeedIngredient("Красный лук", "0.5 шт", true),
                SeedIngredient("Сыр фета", "100 г", true),
                SeedIngredient("Маслины", "50 г", false)
            )
        ),
        SeedRecipe(
            name = "Огуречный салат",
            cookingTime = "10 мин",
            image = "cucumber_salad.jpg",
            ingredients = listOf(
                SeedIngredient("Огурцы", "4 шт", true),
                SeedIngredient("Лук", "1 шт", true),
                SeedIngredient("Укроп", "пучок", false),
                SeedIngredient("Сметана", "2 ст.л", false)
            )
        ),
        SeedRecipe(
            name = "Мясная нарезка",
            cookingTime = "15 мин",
            image = "meat_platter.jpg",
            ingredients = listOf(
                SeedIngredient("Колбаса сырокопчёная", "150 г", true),
                SeedIngredient("Ветчина", "150 г", true),
                SeedIngredient("Карбонад", "100 г", true),
                SeedIngredient("Зелень", "по вкусу", false)
            )
        ),
        SeedRecipe(
            name = "Картофель соломкой",
            cookingTime = "25 мин",
            image = "potato_strips.jpg",
            ingredients = listOf(
                SeedIngredient("Картофель", "4 шт", true),
                SeedIngredient("Растительное масло", "3 ст.л", false),
                SeedIngredient("Соль", "по вкусу", false),
                SeedIngredient("Специи", "по вкусу", false)
            )
        )
    )

    suspend fun seed(baseUrl: String) {
        newSuspendedTransaction(Dispatchers.IO) {
            val alreadySeeded = RecipesTable.selectAll()
                .where { RecipesTable.name eq "Фруктовая нарезка" }
                .count() > 0
            val hasCuttableData = IngredientsTable.selectAll()
                .where { IngredientsTable.cuttable eq true }
                .count() > 0
            if (alreadySeeded && hasCuttableData) return@newSuspendedTransaction

            IngredientsTable.deleteAll()
            RecipesTable.deleteAll()

            val existingAdmin = UsersTable.selectAll()
                .where { UsersTable.email eq "admin@smartcut.local" }
                .singleOrNull()

            val adminId = if (existingAdmin != null) {
                existingAdmin[UsersTable.id]
            } else {
                UsersTable.insert {
                    it[name] = "admin"
                    it[email] = "admin@smartcut.local"
                    it[passwordHash] = org.mindrot.jbcrypt.BCrypt.hashpw("admin123", org.mindrot.jbcrypt.BCrypt.gensalt())
                } get UsersTable.id
            }

            recipes.forEach { recipe ->
                val newId = RecipesTable.insert {
                    it[name] = recipe.name
                    it[cookingTime] = recipe.cookingTime
                    it[imageUrl] = "$baseUrl/images/${recipe.image}"
                    it[userId] = adminId
                } get RecipesTable.id

                recipe.ingredients.forEach { ing ->
                    IngredientsTable.insert {
                        it[name] = ing.name
                        it[amount] = ing.amount
                        it[cuttable] = ing.cuttable
                        it[IngredientsTable.recipeId] = newId
                    }
                }
            }
        }
    }
}
