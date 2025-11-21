package com.chpark.restaurant.auth.infrastructure.redis

import com.chpark.restaurant.auth.domain.port.TokenBlacklistStore
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisTokenBlacklistStore(
    private val redisTemplate: ReactiveStringRedisTemplate
) : TokenBlacklistStore {

    private val ops = redisTemplate.opsForValue()
    private val ttl: Duration = Duration.ofMinutes(30)

    private fun key(
        accessToken: String
    ): String = "auth:blacklist:${accessToken.hashCode()}"

    override suspend fun blacklist(
        accessToken: String
    ) {
        ops.set(
            key(accessToken = accessToken),
            "1",
            ttl
        ).awaitFirstOrNull()
    }

    override suspend fun isBlacklisted(
        accessToken: String
    ): Boolean =
        redisTemplate.hasKey(
            key(
                accessToken = accessToken
            )
        ).awaitFirstOrNull() ?: false
}