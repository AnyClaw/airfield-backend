package com.example.repositories

import com.example.db.dao.AirportDAO
import com.example.db.dao.RouteDAO
import com.example.db.dao.WaypointDAO
import com.example.db.suspendTransaction
import com.example.db.tables.AirportTable
import com.example.models.Airport
import com.example.models.Route
import com.example.models.Waypoint

class MapRepository {
    suspend fun findAllWaypoints(): List<Waypoint> = suspendTransaction {
        WaypointDAO.all().map { it.toModel() }
    }

    suspend fun findAllAirports(): List<Airport> = suspendTransaction {
        AirportDAO.all().map { it.toModel() }
    }

    suspend fun findAllRoutes(): List<Route> = suspendTransaction {
        RouteDAO.all().map { it.toModel() }
    }

    suspend fun findWaypointById(id: Int): Waypoint? = suspendTransaction {
        WaypointDAO.findById(id)?.toModel()
    }

    suspend fun findAirportById(id: Int): Airport? = suspendTransaction {
        AirportDAO.findById(id)?.toModel()
    }

    suspend fun findAirportByIcao(icao: String): Airport? = suspendTransaction {
        AirportDAO.find { AirportTable.icao eq icao }.firstOrNull()?.toModel()
    }
}