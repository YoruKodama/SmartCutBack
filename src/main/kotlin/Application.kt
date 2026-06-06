package com.example.smartcut

import com.example.smartcut.database.DataSeeder
import com.example.smartcut.database.DatabaseFactory
import com.example.smartcut.plugins.configureRouting
import com.example.smartcut.plugins.configureSecurity
import com.example.smartcut.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.http.*
import io.ktor.server.response.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    configureSerialization()
    configureErrorHandling()
    DatabaseFactory.init()
    configureSecurity()
    configureRouting()

    val baseUrl = environment.config.propertyOrNull("app.baseUrl")?.getString() ?: "http://localhost:8080"
    kotlinx.coroutines.runBlocking {
        DataSeeder.seed(baseUrl)
    }
}
fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.message ?: "Ошибка")
        }
        exception<Exception> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Внутренняя ошибка")
        }
    }
}