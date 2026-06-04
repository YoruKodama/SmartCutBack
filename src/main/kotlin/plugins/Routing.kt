package com.example.smartcut.plugins

import com.example.smartcut.routing.auth.authRoutes
import com.example.smartcut.routing.recipes.recipeRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRoutes()
        recipeRoutes()
    }
}