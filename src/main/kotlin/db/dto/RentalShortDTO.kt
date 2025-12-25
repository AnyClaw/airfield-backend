package com.example.db.dto

import com.example.enums.RentalStatus
import com.example.models.Airport
import com.example.models.Plane
import com.example.models.User
import kotlinx.serialization.Serializable

@Serializable
data class RentalShortDTO(
    val id: Int,
    val pilot: User,
    val plane: Plane,
    val startTime: String,
    val endTime: String?,
    val arrivalAirport: Airport,
    val departureAirport: Airport,
    val totalCost: Int?,
    val status: RentalStatus
)