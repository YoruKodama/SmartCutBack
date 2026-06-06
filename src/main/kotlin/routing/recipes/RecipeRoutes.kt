package com.example.smartcut.routing.recipes

import com.example.smartcut.models.dto.RecipeRequest
import com.example.smartcut.models.dto.UploadResponse
import com.example.smartcut.services.RecipeService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.recipeRoutes(recipeService: RecipeService, imagesDir: File, baseUrl: String) {
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

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Неверный id")

                recipeService.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    route("/images") {
        authenticate("auth-jwt") {
            post("/upload") {
                val multipart = call.receiveMultipart()
                var savedUrl: String? = null

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val extension = part.originalFileName?.substringAfterLast('.', "jpg") ?: "jpg"
                        val filename = "${System.currentTimeMillis()}.$extension"
                        val file = File(imagesDir, filename)
                        part.streamProvider().use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        savedUrl = "$baseUrl/images/$filename"
                    }
                    part.dispose()
                }

                val url = savedUrl
                    ?: return@post call.respond(HttpStatusCode.BadRequest, "Файл не получен")
                call.respond(HttpStatusCode.OK, UploadResponse(url))
            }
        }
    }
}
