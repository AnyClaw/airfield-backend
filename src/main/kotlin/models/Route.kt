package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: Int,
    val fromWaypoint: Waypoint,
    val toWaypoint: Waypoint,
    val distance: Float
)
