package com.example.db.dto

import com.example.enums.RentalStatus
import com.example.models.Airport
import com.example.models.Plane
import com.example.models.Route
import com.example.models.User
import kotlinx.serialization.Serializable

@Serializable
data class RentalDTO (
    val id: Int,
    val pilot: User,
    val plane: Plane,
    val startTime: String,
    val endTime: String?,
    val arrivalAirport: Airport,
    val departureAirport: Airport,
    val currentStage: Int,
    val route: List<Route>,
    val isMaintenance: Boolean,
    val maintenanceCost: Int,
    val refuelCost: Int,
    val status: RentalStatus,
    val totalCost: Long
)