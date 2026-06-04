package com.example.smartcut.models.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipeRequest(
    val name: String,
    val cookingTime: String? = null,
    val imageUrl: String? = null,
    val ingredients: List<IngredientRequest> = emptyList()
)

@Serializable
data class IngredientRequest(
    val name: String,
    val amount: String? = null
)

@Serializable
data class RecipeResponse(
    val id: Int,
    val name: String,
    val cookingTime: String?,
    val imageUrl: String?,
    val userId: Int,
    val ingredients: List<IngredientResponse> = emptyList()
)

@Serializable
data class IngredientResponse(
    val id: Int,
    val name: String,
    val amount: String?
)
