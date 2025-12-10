package com.example.db.dto

import kotlinx.serialization.Serializable

@Serializable
data class PilotRegisterDTO(
    val username: String,
    val login: String,
    val password: String,
    val license: String,
    val mileage: Float
)