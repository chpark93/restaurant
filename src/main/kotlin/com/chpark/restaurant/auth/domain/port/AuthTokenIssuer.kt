package com.chpark.restaurant.auth.domain.port

interface AuthTokenIssuer {
    fun issueAccessToken(
        subject: String,
        roles: List<String>
    ): String

    fun issueRefreshToken(
        subject: String,
        roles: List<String>
    ): String
}