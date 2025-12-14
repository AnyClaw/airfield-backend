package com.example.routes

import com.example.db.dto.RentalQueryDTO
import com.example.enums.UserRole
import com.example.repositories.MapRepository
import com.example.repositories.PlanesRepository
import com.example.repositories.RentalRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.configurePlaneCatalogRoutes(
    planeRepository: PlanesRepository,
    rentalRepository: RentalRepository,
) {
    routing {
        route("/planes") {
            get {
                val planes = planeRepository.getAllPlanes()
                call.respond(planes)
            }

            get("/{id}") {
                val planeId = call.parameters["id"]

                if (planeId == null) {
                    call.respond(HttpStatusCode.BadRequest, "Id not found")
                    return@get
                }

                val plane = planeRepository.findById(planeId.toInt())

                if (plane == null) {
                    call.respond(HttpStatusCode.NotFound, "Plane with id=$planeId not found")
                    return@get
                }

                call.respond(plane)
            }
        }

        authenticate("auth-jwt") {
            post("/rent") {
                val principal = call.principal<JWTPrincipal>()!!

                if (!principal.payload.getClaim("role").asString().equals(UserRole.PILOT.name)) {
                    call.respond(HttpStatusCode.Forbidden, "You're not a pilot!")
                    return@post
                }

                val rentalQuery = call.receive<RentalQueryDTO>()
                println(rentalQuery)

                val plane = planeRepository.findById(rentalQuery.planeId)

                if (plane == null) {
                    call.respond(HttpStatusCode.NotFound, "No such plane")
                    return@post
                }

                //FIXME Вернуть нормальный статус
                if (!plane.isAvailable) {
                    call.respond(HttpStatusCode.Forbidden, "Plane is already rented!")
                    return@post
                }

                plane.isAvailable = false
                planeRepository.save(plane)

                val rental = rentalRepository.create(rentalQuery)
                call.respond(rental)
            }

            get("/check") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username! Token is expired at $expiresAt ms.")
            }
        }
    }
}
