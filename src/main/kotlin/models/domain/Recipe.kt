package com.example.smartcut.models.domain

data class Recipe(
    val id: Int,
    val name: String,
    val cookingTime: String?,
    val imageUrl: String?,
    val userId: Int,
    val ingredients: List<Ingredient> = emptyList()
)

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: String?,
    val recipeId: Int
)