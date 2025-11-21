package com.chpark.restaurant.auth.application.dto

data class TokenResult(
    val accessToken: String,
    val refreshToken: String
)