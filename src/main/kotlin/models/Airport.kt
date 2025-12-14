package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Airport(
    val id: Int,
    val name: String,
    val icao: String,
    val waypoint: Waypoint
)
