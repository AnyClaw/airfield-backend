package com.example.db.dto

import com.example.models.Route
import kotlinx.serialization.Serializable

@Serializable
data class FlightRouteDTO(
    val route: List<Route>,
    val distance: Float
)
