package com.example.routes

import com.example.db.dto.FlightRouteDTO
import com.example.repositories.MapRepository
import com.example.services.MapService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.configureMapRoutes(mapRepository: MapRepository) {
    routing {
        route("/map") {
            get("/waypoints") {
                call.respond(mapRepository.findAllWaypoints())
            }

            get("/airports") {
                call.respond(mapRepository.findAllAirports())
            }

            get("/routes") {
                call.respond(mapRepository.findAllRoutes())
            }
        }

        get("/airport/{icao}") {
            val icao = call.parameters["icao"]

            if (icao == null) {
                call.respond(HttpStatusCode.BadRequest, "Icao isn't present")
                return@get
            }

            val airport = mapRepository.findAirportByIcao(icao)

            if (airport == null) {
                call.respond(HttpStatusCode.NotFound, "Airport not found")
                println(icao)
                return@get
            }

            call.respond(airport)
        }

        get("/flight/build") {
            val arrivalAirportIcao = call.request.queryParameters["arrivalIcao"]
            val departureAirportIcao = call.request.queryParameters["departureIcao"]

            if (arrivalAirportIcao == null || departureAirportIcao == null) {
                call.respond(HttpStatusCode.BadRequest, "Request params isn't present")
                return@get
            }

            val arrivalAirport = mapRepository.findAirportByIcao(arrivalAirportIcao)
            val departureAirport = mapRepository.findAirportByIcao(departureAirportIcao)

            if (arrivalAirport == null || departureAirport == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            val mapService = MapService(mapRepository.findAllWaypoints(), mapRepository.findAllRoutes())
            val flightRoute =mapService.buildFlightRoute(arrivalAirport, departureAirport)
            val distance = mapService.calculateRouteDistance(flightRoute)

            call.respond(FlightRouteDTO(flightRoute, distance))
        }
    }
}