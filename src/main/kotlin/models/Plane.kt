package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Plane(
    val id: Int,
    val name: String,
    val tankCapacity: Float,
    val fuel: Float,
    var isAvailable: Boolean
)