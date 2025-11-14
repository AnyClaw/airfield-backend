package com.example.db.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterDTO(
    val username: String,
    val login: String,
    val password: String
)