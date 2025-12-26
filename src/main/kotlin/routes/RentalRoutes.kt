package com.example.routes

import com.example.db.dto.RentalQueryDTO
import com.example.enums.Condition
import com.example.enums.RentalStatus
import com.example.enums.UserRole
import com.example.repositories.PilotRepository
import com.example.repositories.PlanesRepository
import com.example.repositories.RentalRepository
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

fun Application.configureRentalRoutes(
    planeRepository: PlanesRepository,
    rentalRepository: RentalRepository,
    pilotRepository: PilotRepository
) {
    routing {
        authenticate("auth-jwt") {
            post("/rent") {
                val principal = call.principal<JWTPrincipal>()!!

                if (!principal.payload.getClaim("role").asString().equals(UserRole.PILOT.name)) {
                    call.respond(HttpStatusCode.Forbidden, "You're not a pilot!")
                    return@post
                }

                val rentalQuery = call.receive<RentalQueryDTO>()
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

                val rental = rentalRepository.create(rentalQuery)
                plane.isAvailable = false
                planeRepository.update(plane)

                call.respond(rental)
            }

            get("/rentals/{id}") {
                val userId = call.parameters["id"]

                if (userId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }

                val pilot = pilotRepository.findByUserId(userId.toInt())

                if (pilot == null) {
                    call.respond(HttpStatusCode.NotFound, "No such pilot")
                    return@get
                }

                val rentals = rentalRepository.findAllByUserId(pilot.user.id)

                call.respond(rentals)
            }

            route("/rental") {
                get("/{id}") {
                    val rentalId = call.parameters["id"]

                    if (rentalId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@get
                    }

                    val rental = rentalRepository.findById(rentalId.toInt())
                    val principal = call.principal<JWTPrincipal>()!!

                    if (principal.payload.getClaim("id").asInt() != rental.pilot.id) {
                        call.respond(HttpStatusCode.Forbidden)
                        return@get
                    }

                    call.respond(rental)
                }

                post ("/takeoff/{id}") {
                    val rentalId = call.parameters["id"]

                    if (rentalId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }

                    val rental = rentalRepository.findById(rentalId.toInt())
                    val principal = call.principal<JWTPrincipal>()!!

                    if (principal.payload.getClaim("id").asInt() != rental.pilot.id) {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    val plane = planeRepository.findById(rental.plane.id)

                    if (plane == null) {
                        call.respond(HttpStatusCode.NotFound, "No such plane")
                        return@post
                    }

                    plane.fuel += rental.refuelCost / 100
                    if (rental.isMaintenance) plane.condition = Condition.EXCELLENT

                    planeRepository.update(plane)!!

                    call.respond(rentalRepository.changeStatus(rental.id, RentalStatus.ACTIVE))
                }

                post("/next/{id}") {
                    val rentalId = call.parameters["id"]

                    if (rentalId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }

                    val rental = rentalRepository.findById(rentalId.toInt())
                    val principal = call.principal<JWTPrincipal>()!!

                    if (principal.payload.getClaim("id").asInt() != rental.pilot.id) {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    if (rental.currentStage == rental.route.size) {
                        call.respond(HttpStatusCode.BadRequest, "It is final stage")
                        return@post
                    }

                    val plane = planeRepository.findById(rental.plane.id)

                    if (plane == null) {
                        call.respond(HttpStatusCode.NotFound, "No such plane")
                        return@post
                    }

                    plane.fuel -= rental.route[rental.currentStage].distance * plane.fuelConsumption / 1000
                    planeRepository.update(plane)!!

                    call.respond(rentalRepository.nextStage(rental.id))
                }

                post("/land/{id}") {
                    val rentalId = call.parameters["id"]

                    if (rentalId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }

                    val rental = rentalRepository.findById(rentalId.toInt())
                    val principal = call.principal<JWTPrincipal>()!!

                    if (principal.payload.getClaim("id").asInt() != rental.pilot.id) {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    val plane = planeRepository.findById(rental.plane.id)

                    if (plane == null) {
                        call.respond(HttpStatusCode.NotFound, "No such plane")
                        return@post
                    }

                    plane.condition = when (plane.condition) {
                        Condition.EXCELLENT -> Condition.GOOD
                        Condition.GOOD -> Condition.FAIR
                        Condition.FAIR -> Condition.POOR
                        else -> Condition.POOR
                    }

                    plane.isAvailable = true
                    planeRepository.update(plane)!!

                    call.respond(rentalRepository.finishFlight(rental.id))
                }

                post("/pay/{id}") {
                    val rentalId = call.parameters["id"]

                    if (rentalId == null) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@post
                    }

                    val rental = rentalRepository.findById(rentalId.toInt())
                    val principal = call.principal<JWTPrincipal>()!!

                    if (principal.payload.getClaim("id").asInt() != rental.pilot.id) {
                        call.respond(HttpStatusCode.Forbidden)
                        return@post
                    }

                    call.respond(rentalRepository.changeStatus(rental.id, RentalStatus.COMPLETED))
                }
            }
        }
    }
}