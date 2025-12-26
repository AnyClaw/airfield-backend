package com.example.plugins

import com.example.repositories.MapRepository
import com.example.repositories.PilotRepository
import com.example.repositories.PlanesRepository
import com.example.repositories.RentalRepository
import com.example.repositories.UsersRepository
import com.example.routes.configureAccountantRoutes
import com.example.routes.configureAdminRoutes
import com.example.routes.configureAuthRoutes
import com.example.routes.configureMapRoutes
import com.example.routes.configurePilotRoutes
import com.example.routes.configurePlaneCatalogRoutes
import com.example.routes.configureRentalRoutes
import io.ktor.server.application.Application

fun Application.configureRouting() {
    val userRepository = UsersRepository()
    val pilotRepository = PilotRepository()
    val planesRepository = PlanesRepository()
    val rentalRepository = RentalRepository()

    configureAuthRoutes(userRepository)
    configurePlaneCatalogRoutes(PlanesRepository())
    configurePilotRoutes(PilotRepository())
    configureAccountantRoutes(userRepository, rentalRepository)
    configureMapRoutes(MapRepository())
    configureRentalRoutes(planesRepository, rentalRepository, pilotRepository)
    configureAdminRoutes(userRepository, planesRepository)
}