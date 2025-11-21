package com.chpark.restaurant.auth.infrastructure.redis

import com.chpark.restaurant.auth.domain.port.RefreshTokenStore
import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisRefreshTokenStore(
    private val redisTemplate: ReactiveStringRedisTemplate
) : RefreshTokenStore {

    private val opsForValueRedis = redisTemplate.opsForValue()
    private val ttl: Duration = Duration.ofDays(7)

    companion object {
        private const val KEY_PREFIX = "auth:token"
    }

    override suspend fun save(
        userId: String,
        refreshToken: String
    ) {
        opsForValueRedis.set(
            key(
                userId = userId
            ),
            refreshToken,
            ttl
        ).awaitFirstOrNull()
    }

    override suspend fun exists(
        userId: String,
        refreshToken: String
    ): Boolean = redisTemplate.hasKey(
        key(
            userId = userId
        )
    ).awaitFirstOrNull() ?: false

    override suspend fun delete(
        userId: String,
        refreshToken: String?
    ) {
        val key = key(
            userId = userId
        )

        if (refreshToken != null) {
            val storedToken = redisTemplate.opsForValue().get(key).awaitFirstOrNull()

            if (storedToken == refreshToken) {
                redisTemplate.delete(key).awaitFirstOrNull()
            } else {
                throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN)
            }

        } else {
            redisTemplate.delete(key).awaitFirstOrNull()
        }
    }

    private fun key(
        userId: String
    ): String = "$KEY_PREFIX:$userId"
}