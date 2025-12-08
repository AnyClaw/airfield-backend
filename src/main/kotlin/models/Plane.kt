package com.example.models

import com.example.enums.Condition
import kotlinx.serialization.Serializable

@Serializable
data class Plane(
    val id: Int,
    val name: String,
    val tankCapacity: Float,
    var fuel: Float,
    var isAvailable: Boolean,
    var rating: Float,
    val rentalCost: Double,
    val fuelConsumption: Float, // расход топлива
    val maintenanceCost: Float, // стоимость ТО
    var condition: Condition, // состояние
    var mileage: Float // пробег
)