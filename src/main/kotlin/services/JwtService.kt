package com.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.User
import java.util.Date

class JwtService(
    private val secret: String,
    private val issuer: String,
    private val audience: String
) {
    fun generateToken(user: User): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("id", user.id)
            .withClaim("username", user.name)
            .withClaim("login", user.login)
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 60000*10))
            .sign(Algorithm.HMAC256(secret))
    }
}