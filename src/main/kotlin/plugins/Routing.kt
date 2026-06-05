package com.example.smartcut.plugins

import com.example.smartcut.repositories.RecipeRepository
import com.example.smartcut.repositories.UserRepository
import com.example.smartcut.routing.auth.authRoutes
import com.example.smartcut.routing.recipes.recipeRoutes
import com.example.smartcut.services.AuthService
import com.example.smartcut.services.RecipeService
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val userRepository = UserRepository()
    val recipeRepository = RecipeRepository()
    val authService = AuthService(userRepository)
    val recipeService = RecipeService(recipeRepository)
    routing {
        authRoutes(authService)
        recipeRoutes(recipeService)
    }
}