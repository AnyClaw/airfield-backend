package com.example.routes

import com.example.db.dto.UserLoginDTO
import com.example.db.dto.PilotRegisterDTO
import com.example.repositories.UsersRepository
import com.example.services.JwtService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.mindrot.jbcrypt.BCrypt

fun Application.configureAuthRoutes(userRepository: UsersRepository) {
    routing {
        post("/login") {
            val credentials = call.receive<UserLoginDTO>()
            val userAuthData = userRepository.findAuthData(credentials.login)

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
            val credentials = call.receive<PilotRegisterDTO>()

            if (userRepository.findAuthData(credentials.login) != null) {
                call.respond(HttpStatusCode.Conflict, "User with this login already exists")
                return@post
            }

            val hashedPassword = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
            userRepository.createUser(PilotRegisterDTO(
                credentials.username,
                credentials.login,
                hashedPassword,
                credentials.license,
                credentials.mileage
            ))

            call.respond(HttpStatusCode.Created)
        }

        get("/encrypt/{pass}") {
            call.respond(BCrypt.hashpw(call.parameters["pass"] ?: "", BCrypt.gensalt()))
        }
    }
}