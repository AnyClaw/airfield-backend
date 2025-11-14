package com.example.models

import com.example.enums.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val login: String,
    val role: UserRole
)
