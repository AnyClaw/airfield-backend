package com.example.models

import com.example.enums.RentalStatus
import java.time.LocalDateTime

class Rental(
    val pilot: User,
    val plane: Plane,
    val startTime: LocalDateTime,
    var endTime: LocalDateTime?,
    val arrivalAirport: Airport,
    val departureAirport: Airport,
    val isMaintenance: Boolean,
    val maintenanceCost: Int,
    val refuelCost: Int,
    var status: RentalStatus
){
    fun finish() {
        endTime = LocalDateTime.now()
        status = RentalStatus.COMPLETED
    }
}