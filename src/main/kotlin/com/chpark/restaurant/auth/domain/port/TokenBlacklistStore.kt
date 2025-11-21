package com.chpark.restaurant.auth.domain.port

interface TokenBlacklistStore {
    suspend fun blacklist(
        accessToken: String
    )

    suspend fun isBlacklisted(
        accessToken: String
    ): Boolean
}