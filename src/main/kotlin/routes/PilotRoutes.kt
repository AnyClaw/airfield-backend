package com.example.routes

import com.example.repositories.PilotRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configurePilotRoutes(repository: PilotRepository) {
    routing {
        authenticate("auth-jwt") {
            route("/pilot") {
                get("/profile") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val userId = principal.payload.getClaim("id").asInt()

                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                        return@get
                    }

                    val user = repository.findByUserId(userId)

                    if (user == null) {
                        call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                        return@get
                    }

                    call.respond(user)
                }
            }
        }
    }
}