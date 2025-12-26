package com.example.routes

import com.example.db.dto.PlaneCreationDTO
import com.example.enums.UserRole
import com.example.models.Plane
import com.example.repositories.PlanesRepository
import com.example.repositories.UsersRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureAdminRoutes(userRepository: UsersRepository, planesRepository: PlanesRepository) {
    routing {
        authenticate("auth-jwt") {
            route("/admin") {
                get("/profile") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val userId = principal.payload.getClaim("id").asInt()

                    if (!principal.payload.getClaim("role").asString().equals(UserRole.ADMINISTRATOR.name)) {
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

                post("/add") {
                    val principal = call.principal<JWTPrincipal>()!!

                    if (!principal.payload.getClaim("role").asString().equals(UserRole.ADMINISTRATOR.name)) {
                        call.respond(HttpStatusCode.Forbidden, "You're not a accountant!")
                        return@post
                    }

                    val plane = call.receive<PlaneCreationDTO>()
                    call.respond(planesRepository.save(plane))
                }
            }
        }
    }
}