package com.example.db.dto

import com.example.enums.Condition
import kotlinx.serialization.Serializable

@Serializable
data class PlaneCreationDTO(
    val name: String,
    val tankCapacity: Float,
    val fuel: Float,
    val rentalCost: Double,
    val fuelConsumption: Float,
    val maintenanceCost: Float,
    val condition: Condition,
    val mileage: Float
)
