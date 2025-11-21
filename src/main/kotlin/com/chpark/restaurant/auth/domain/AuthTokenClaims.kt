package com.chpark.restaurant.auth.domain

data class AuthTokenClaims(
    val subject: String,
    val roles: List<String>,
    val tokenType: TokenType
) {
    enum class TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }
}