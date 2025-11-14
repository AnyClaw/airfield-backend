package com.example.db.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserLoginDTO(
    val login: String,
    val password: String
)
