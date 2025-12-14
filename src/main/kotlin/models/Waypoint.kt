package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Waypoint(
    val id: Int,
    val name: String,
    val x: Float,
    val y: Float
)