package com.example.smartcut.repositories

import com.example.smartcut.database.tables.IngredientsTable
import com.example.smartcut.database.tables.RecipesTable
import com.example.smartcut.models.domain.Ingredient
import com.example.smartcut.models.domain.Recipe
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class RecipeRepository {

    suspend fun create(name: String, cookingTime: String?, imageUrl: String?, userId: Int): Recipe {
        return newSuspendedTransaction(Dispatchers.IO) {
            val id = RecipesTable.insert {
                it[RecipesTable.name] = name
                it[RecipesTable.cookingTime] = cookingTime
                it[RecipesTable.imageUrl] = imageUrl
                it[RecipesTable.userId] = userId
            } get RecipesTable.id
            Recipe(id, name, cookingTime, imageUrl, userId)
        }
    }

    suspend fun addIngredient(name: String, amount: String?, recipeId: Int, cuttable: Boolean = false): Ingredient {
        return newSuspendedTransaction(Dispatchers.IO) {
            val id = IngredientsTable.insert {
                it[IngredientsTable.name] = name
                it[IngredientsTable.amount] = amount
                it[IngredientsTable.recipeId] = recipeId
                it[IngredientsTable.cuttable] = cuttable
            } get IngredientsTable.id
            Ingredient(id, name, amount, recipeId, cuttable)
        }
    }

    suspend fun getAll(): List<Recipe> {
        return newSuspendedTransaction(Dispatchers.IO) {
            RecipesTable.selectAll().map { row ->
                val recipe = row.toRecipe()
                val ingredients = IngredientsTable.selectAll()
                    .where { IngredientsTable.recipeId eq recipe.id }
                    .map { it.toIngredient() }
                recipe.copy(ingredients = ingredients)
            }
        }
    }

    suspend fun getById(id: Int): Recipe? {
        return newSuspendedTransaction(Dispatchers.IO) {
            val recipe = RecipesTable.selectAll().where { RecipesTable.id eq id }
                .map { it.toRecipe() }
                .singleOrNull() ?: return@newSuspendedTransaction null

            val ingredients = IngredientsTable.selectAll()
                .where { IngredientsTable.recipeId eq id }
                .map { it.toIngredient() }

            recipe.copy(ingredients = ingredients)
        }
    }

    suspend fun delete(id: Int) {
        newSuspendedTransaction(Dispatchers.IO) {
            IngredientsTable.deleteWhere { IngredientsTable.recipeId eq id }
            RecipesTable.deleteWhere { RecipesTable.id eq id }
        }
    }

    private fun ResultRow.toRecipe() = Recipe(
        id = this[RecipesTable.id],
        name = this[RecipesTable.name],
        cookingTime = this[RecipesTable.cookingTime],
        imageUrl = this[RecipesTable.imageUrl],
        userId = this[RecipesTable.userId]
    )

    private fun ResultRow.toIngredient() = Ingredient(
        id = this[IngredientsTable.id],
        name = this[IngredientsTable.name],
        amount = this[IngredientsTable.amount],
        recipeId = this[IngredientsTable.recipeId],
        cuttable = this[IngredientsTable.cuttable]
    )
}
