package com.example.models

import com.example.db.dto.RentalDTO
import com.example.enums.RentalStatus
import java.time.LocalDateTime

class Rental(
    val pilot: User,
    val plane: Plane,
    val startTime: LocalDateTime,
    var endTime: LocalDateTime?,
    var status: RentalStatus
){
    fun finish() {
        endTime = LocalDateTime.now()
        status = RentalStatus.COMPLETED
    }

    fun toDTO() = RentalDTO(pilot, plane, startTime.toString(), endTime.toString(), status)
}