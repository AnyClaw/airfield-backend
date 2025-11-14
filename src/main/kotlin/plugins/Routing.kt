package com.example.plugins

import com.example.repositories.PlanesRepository
import com.example.repositories.RentalRepository
import com.example.repositories.UsersRepository
import com.example.routes.configureAuthRoutes
import com.example.routes.configurePlaneCatalogRoutes
import io.ktor.server.application.Application

fun Application.configureRouting() {
    configureAuthRoutes(UsersRepository())
    configurePlaneCatalogRoutes(PlanesRepository(), RentalRepository())
}