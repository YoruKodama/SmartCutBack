package com.example.smartcut.services

import com.example.smartcut.models.dto.IngredientResponse
import com.example.smartcut.models.dto.RecipeRequest
import com.example.smartcut.models.dto.RecipeResponse
import com.example.smartcut.repositories.RecipeRepository

class RecipeService(private val recipeRepository: RecipeRepository) {

    suspend fun getAll(): List<RecipeResponse> {
        return recipeRepository.getAll().map { recipe ->
            RecipeResponse(
                id = recipe.id,
                name = recipe.name,
                cookingTime = recipe.cookingTime,
                imageUrl = recipe.imageUrl,
                userId = recipe.userId,
                ingredients = recipe.ingredients.map { ingredient ->
                    IngredientResponse(
                        id = ingredient.id,
                        name = ingredient.name,
                        amount = ingredient.amount,
                        cuttable = ingredient.cuttable
                    )
                }
            )
        }
    }

    suspend fun getById(id: Int): RecipeResponse? {
        val recipe = recipeRepository.getById(id) ?: return null
        return RecipeResponse(
            id = recipe.id,
            name = recipe.name,
            cookingTime = recipe.cookingTime,
            imageUrl = recipe.imageUrl,
            userId = recipe.userId,
            ingredients = recipe.ingredients.map { ingredient ->
                IngredientResponse(
                    id = ingredient.id,
                    name = ingredient.name,
                    amount = ingredient.amount,
                    cuttable = ingredient.cuttable
                )
            }
        )
    }

    suspend fun create(request: RecipeRequest, userId: Int): RecipeResponse {
        val recipe = recipeRepository.create(
            name = request.name,
            cookingTime = request.cookingTime,
            imageUrl = request.imageUrl,
            userId = userId
        )

        val ingredients = request.ingredients.map { ingredientRequest ->
            recipeRepository.addIngredient(
                name = ingredientRequest.name,
                amount = ingredientRequest.amount,
                recipeId = recipe.id,
                cuttable = ingredientRequest.cuttable
            )
        }

        return RecipeResponse(
            id = recipe.id,
            name = recipe.name,
            cookingTime = recipe.cookingTime,
            imageUrl = recipe.imageUrl,
            userId = recipe.userId,
            ingredients = ingredients.map { ingredient ->
                IngredientResponse(
                    id = ingredient.id,
                    name = ingredient.name,
                    amount = ingredient.amount,
                    cuttable = ingredient.cuttable
                )
            }
        )
    }

    suspend fun delete(id: Int) {
        recipeRepository.delete(id)
    }
}
