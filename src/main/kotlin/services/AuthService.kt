package com.example.smartcut.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.smartcut.models.domain.User
import com.example.smartcut.models.dto.AuthResponse
import com.example.smartcut.models.dto.LoginRequest
import com.example.smartcut.models.dto.RegisterRequest
import com.example.smartcut.plugins.JWT_AUDIENCE //константа для кого выдал токен
import com.example.smartcut.plugins.JWT_ISSUER //кто выдал токен
import com.example.smartcut.plugins.JWT_SECRET //секретный ключ
import com.example.smartcut.repositories.UserRepository
import java.util.Date

class AuthService(private val userRepository: UserRepository) {

    suspend fun register(request: RegisterRequest): AuthResponse {
        val existing = userRepository.findByEmail(request.email)
        if (existing != null) error("Пользователь с таким email уже существует")
        //регистрация нового пользователя хэшируем пароль, создаем в бд, возвращаем токен
        val passwordHash = hashPassword(request.password)
        val user = userRepository.create(request.name, request.email, passwordHash)
        val token = generateToken(user)

        return AuthResponse(token, user.id, user.name)
    }

    suspend fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: error("Пользователь не найден")

        if (!verifyPassword(request.password, user.passwordHash)) {
            error("Неверный пароль")
        }

        val token = generateToken(user)
        return AuthResponse(token, user.id, user.name)
    }

    private fun generateToken(user: User): String {
        return JWT.create()
            .withIssuer(JWT_ISSUER)
            .withAudience(JWT_AUDIENCE)
            .withClaim("userId", user.id)
            .withExpiresAt(Date(System.currentTimeMillis() + 86_400_000))
            .sign(Algorithm.HMAC256(JWT_SECRET))
    }

    private fun hashPassword(password: String): String {
        return org.mindrot.jbcrypt.BCrypt.hashpw(password, org.mindrot.jbcrypt.BCrypt.gensalt())
    }

    private fun verifyPassword(password: String, hash: String): Boolean {
        return org.mindrot.jbcrypt.BCrypt.checkpw(password, hash)
    }
}