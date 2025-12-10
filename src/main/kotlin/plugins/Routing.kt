package com.example.plugins

import com.example.repositories.PilotRepository
import com.example.repositories.PlanesRepository
import com.example.repositories.RentalRepository
import com.example.repositories.UsersRepository
import com.example.routes.configureAuthRoutes
import com.example.routes.configurePilotRoutes
import com.example.routes.configurePlaneCatalogRoutes
import io.ktor.server.application.Application

fun Application.configureRouting() {
    val pilotRepository = PilotRepository()

    configureAuthRoutes(UsersRepository(), pilotRepository)
    configurePlaneCatalogRoutes(PlanesRepository(), RentalRepository())
    configurePilotRoutes(PilotRepository())
}