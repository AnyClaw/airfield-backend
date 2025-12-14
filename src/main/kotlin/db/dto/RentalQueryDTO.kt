package com.example.db.dto

import com.example.models.Route
import kotlinx.serialization.Serializable

@Serializable
data class RentalQueryDTO(
    val userId: Int,
    val planeId: Int,
    val arrivalAirportId: Int,
    val departureAirportId: Int,
    val flightRoute: List<Route>,
    val distance: Float,
    val refuel: Int,
    val maintenance: Boolean,
    val maintenanceCost: Int
)
