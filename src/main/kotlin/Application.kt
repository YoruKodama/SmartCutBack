package com.example.smartcut

import com.example.smartcut.database.DatabaseFactory
import com.example.smartcut.plugins.configureRouting
import com.example.smartcut.plugins.configureSecurity
import com.example.smartcut.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureSecurity()
    configureRouting()
}