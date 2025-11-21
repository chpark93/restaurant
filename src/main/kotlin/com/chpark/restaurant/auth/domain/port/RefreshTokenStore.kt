package com.chpark.restaurant.auth.domain.port

interface RefreshTokenStore {
    suspend fun save(
        userId: String,
        refreshToken: String
    )

    suspend fun exists(
        userId: String,
        refreshToken: String
    ): Boolean

    suspend fun delete(
        userId: String,
        refreshToken: String?
    )
}