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

    private val opsForValueRedis = redisTemplate.opsForValue()
    private val ttl: Duration = Duration.ofMinutes(30)

    companion object {
        private const val KEY_PREFIX = "auth:blacklist"
        private const val VALUE_BLACK_LIST = "BLACK"
    }

    override suspend fun blacklist(
        accessToken: String
    ) {
        opsForValueRedis.set(
            key(
                accessToken = accessToken
            ),
            VALUE_BLACK_LIST,
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

    private fun key(
        accessToken: String
    ): String = "${KEY_PREFIX}:$accessToken"
}