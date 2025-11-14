package com.example.db.dto

import com.example.enums.RentalStatus
import com.example.models.Plane
import com.example.models.User
import kotlinx.serialization.Serializable

@Serializable
data class RentalDTO (
    val pilot: User,
    val plane: Plane,
    val startTime: String,
    val endTime: String?,
    val status: RentalStatus
)