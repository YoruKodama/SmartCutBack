package com.example.smartcut.routing.recipes

import com.example.smartcut.models.dto.RecipeRequest
import com.example.smartcut.services.RecipeService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.recipeRoutes(recipeService: RecipeService) {
    route("/recipes") {
        authenticate("auth-jwt") {
            get {
                val recipes = recipeService.getAll()
                call.respond(HttpStatusCode.OK, recipes)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Неверный id")

                val recipe = recipeService.getById(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Рецепт не найден")

                call.respond(HttpStatusCode.OK, recipe)
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val request = call.receive<RecipeRequest>()
                val recipe = recipeService.create(request, userId)
                call.respond(HttpStatusCode.Created, recipe)
            }
        }
    }
}