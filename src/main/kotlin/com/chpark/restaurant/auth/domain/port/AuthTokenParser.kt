package com.chpark.restaurant.auth.domain.port

import com.chpark.restaurant.auth.domain.AuthTokenClaims

interface AuthTokenParser {
    fun parse(
        token: String
    ): AuthTokenClaims
}