package com.chpark.restaurant.auth.presentation.dto

object AuthDtos {
    data class ReissueRequest(
        val refreshToken: String
    )

    data class LogoutRequest(
        val accessToken: String,
        val refreshToken: String? = null
    )
}