package com.example.routes

import com.example.enums.UserRole
import com.example.repositories.RentalRepository
import com.example.repositories.UsersRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureAccountantRoutes(userRepository: UsersRepository, rentalRepository: RentalRepository) {
    routing {
        authenticate("auth-jwt") {
            route("/accountant") {
                get("/profile") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val userId = principal.payload.getClaim("id").asInt()

                    if (!principal.payload.getClaim("role").asString().equals(UserRole.ACCOUNTANT.name)) {
                        call.respond(HttpStatusCode.Forbidden, "You're not a accountant!")
                        return@get
                    }

                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                        return@get
                    }

                    val user = userRepository.findById(userId)

                    if (user == null) {
                        call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                        return@get
                    }

                    call.respond(user)
                }

                get("/rentals") {
                    val principal = call.principal<JWTPrincipal>()!!
                    if (!principal.payload.getClaim("role").asString().equals(UserRole.ACCOUNTANT.name)) {
                        call.respond(HttpStatusCode.Forbidden, "You're not a accountant!")
                        return@get
                    }

                    call.respond(rentalRepository.findAll())
                }
            }
        }
    }
}