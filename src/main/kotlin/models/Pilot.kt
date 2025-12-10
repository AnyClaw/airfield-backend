package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Pilot(
    val user: User,
    val license: String,
    var mileage: Float,
    var balance: Float
)
