package com.chpark.restaurant.auth.application.dto

data class RegisterCommand(
    val email: String,
    val password: String,
    val name: String
)