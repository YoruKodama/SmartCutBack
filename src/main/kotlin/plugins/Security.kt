package com.example.smartcut.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

const val JWT_SECRET = "smartcut_secret"
const val JWT_ISSUER = "smartcut"
const val JWT_AUDIENCE = "smartcut_users"

fun Application.configureSecurity() {
    authentication {
        jwt("auth-jwt") {
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withIssuer(JWT_ISSUER)
                    .withAudience(JWT_AUDIENCE)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}