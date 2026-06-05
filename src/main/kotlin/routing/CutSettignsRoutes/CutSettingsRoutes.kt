package com.example.smartcut.routing.cutSettings

import com.example.smartcut.models.dto.CutSettingsRequest
import com.example.smartcut.services.CutSettingsService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.cutSettingsRoutes(cutSettingsService: CutSettingsService) {
    route("/cut-settings") {
        authenticate("auth-jwt") {
            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val settings = cutSettingsService.get(userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Настройки не найдены")

                call.respond(HttpStatusCode.OK, settings)
            }

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val request = call.receive<CutSettingsRequest>()
                val settings = cutSettingsService.save(userId, request)
                call.respond(HttpStatusCode.OK, settings)
            }
        }
    }
}