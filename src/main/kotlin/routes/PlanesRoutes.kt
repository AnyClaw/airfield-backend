package com.example.routes

import com.example.repositories.PlanesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Application.configurePlaneCatalogRoutes(planeRepository: PlanesRepository) {
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
    }
}
