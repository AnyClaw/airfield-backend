package com.example.models

import com.example.db.dto.RentalShortDTO
import com.example.db.dto.RentalDTO
import com.example.enums.RentalStatus
import java.time.LocalDateTime

class Rental(
    val id: Int,
    val pilot: User,
    val plane: Plane,
    val startTime: LocalDateTime,
    var endTime: LocalDateTime?,
    val arrivalAirport: Airport,
    val departureAirport: Airport,
    val currentStage: Int,
    val isMaintenance: Boolean,
    val maintenanceCost: Int,
    val refuelCost: Int,
    var status: RentalStatus
){
    fun finish() {
        endTime = LocalDateTime.now()
        status = RentalStatus.COMPLETED
    }

    fun toShortDto() = RentalShortDTO(
        id,
        pilot,
        plane,
        startTime.toString(),
        endTime.toString(),
        arrivalAirport,
        departureAirport,
        null,
        status
    )
}