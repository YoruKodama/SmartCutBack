package com.example.smartcut.plugins

import com.example.smartcut.repositories.CutSettingsRepository
import com.example.smartcut.repositories.RecipeRepository
import com.example.smartcut.repositories.UserRepository
import com.example.smartcut.routing.auth.authRoutes
import com.example.smartcut.routing.recipes.recipeRoutes
import com.example.smartcut.services.AuthService
import com.example.smartcut.services.RecipeService
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import com.example.smartcut.routing.cutSettings.cutSettingsRoutes
import com.example.smartcut.services.CutSettingsService
import java.io.File

fun Application.configureRouting() {
    val baseUrl = environment.config.propertyOrNull("app.baseUrl")?.getString() ?: "http://localhost:8080"
    val imagesDir = File("src/main/resources/images").also { it.mkdirs() }

    val userRepository = UserRepository()
    val recipeRepository = RecipeRepository()
    val cutSettingsRepository = CutSettingsRepository()
    val authService = AuthService(userRepository)
    val recipeService = RecipeService(recipeRepository)
    val cutSettingsService = CutSettingsService(cutSettingsRepository)

    routing {
        staticFiles("/images", imagesDir)
        authRoutes(authService)
        recipeRoutes(recipeService, imagesDir, baseUrl)
        cutSettingsRoutes(cutSettingsService)
    }
}
