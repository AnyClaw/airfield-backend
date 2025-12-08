package com.example.routes

import com.example.db.dto.UserLoginDTO
import com.example.db.dto.UserRegisterDTO
import com.example.repositories.UsersRepository
import com.example.services.JwtService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.mindrot.jbcrypt.BCrypt

fun Application.configureAuthRoutes(repository: UsersRepository) {
    routing {
        post("/login") {
            val credentials = call.receive<UserLoginDTO>()
            val userAuthData = repository.findAuthData(credentials.login)

            if (userAuthData == null) {
                call.respond(HttpStatusCode.Unauthorized, "No such user")
                return@post
            }

            if (!BCrypt.checkpw(credentials.password, userAuthData.second)) {
                call.respond(HttpStatusCode.Unauthorized, "Incorrect password")
                return@post
            }

            val secret = environment.config.property("jwt.secret").getString()
            val issuer = environment.config.property("jwt.issuer").getString()
            val audience = environment.config.property("jwt.audience").getString()

            val token = JwtService(secret, issuer, audience).generateToken(userAuthData.first)
            call.respond(hashMapOf("token" to token))
        }

        post("/register") {
            val credentials = call.receive<UserRegisterDTO>()

            if (repository.findAuthData(credentials.login) != null) {
                call.respond(HttpStatusCode.Conflict, "User with this login already exists")
                return@post
            }

            val hashedPassword = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
            repository.createUser(UserRegisterDTO(credentials.username, credentials.login, hashedPassword))

            call.respond(HttpStatusCode.Created)
        }

        authenticate("auth-jwt") {
            get("/profile") {
                val principal = call.principal<JWTPrincipal>()!!
                val userId = principal.payload.getClaim("id").asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                    return@get
                }

                val user = repository.findById(userId)

                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                    return@get
                }

                call.respond(user)
            }
        }

        get("/encrypt/{pass}") {
            call.respond(BCrypt.hashpw(call.parameters["pass"] ?: "", BCrypt.gensalt()))
        }
    }
}