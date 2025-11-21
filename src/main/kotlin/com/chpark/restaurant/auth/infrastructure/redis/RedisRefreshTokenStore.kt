package com.chpark.restaurant.auth.infrastructure.redis

import com.chpark.restaurant.auth.domain.port.RefreshTokenStore
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.Duration

@Component
class RedisRefreshTokenStore(
    private val redisTemplate: ReactiveStringRedisTemplate
) : RefreshTokenStore {

    private val opsForValueRedis = redisTemplate.opsForValue()

    private fun key(
        userId: String,
        refreshToken: String
    ): String = "auth:refresh:$userId:${refreshToken.hashCode()}"

    private val ttl: Duration = Duration.ofDays(7)

    override suspend fun save(
        userId: String,
        refreshToken: String
    ) {
        opsForValueRedis.set(
            key(
                userId = userId,
                refreshToken = refreshToken
            ),
            "1",
            ttl
        ).awaitFirstOrNull()
    }

    override suspend fun exists(
        userId: String,
        refreshToken: String
    ): Boolean =
        redisTemplate.hasKey(
            key(
                userId = userId,
                refreshToken = refreshToken
            )
        ).awaitFirstOrNull() ?: false

    override suspend fun delete(
        userId: String,
        refreshToken: String?
    ) {
        if (refreshToken != null) {
            redisTemplate.delete(
                key(
                    userId = userId,
                    refreshToken = refreshToken
                )
            ).awaitFirstOrNull()
        } else {
            val pattern = "auth:refresh:$userId:*"

            val scanOptions = ScanOptions.scanOptions()
                .match(pattern)
                .count(1000)
                .build()

            val keys = redisTemplate.scan(scanOptions)
                .collectList()
                .awaitFirstOrNull()
                .orEmpty()

            if (keys.isNotEmpty()) {
                val keyFlux = Flux.fromIterable(keys)

                redisTemplate.delete(keyFlux).awaitFirstOrNull()
            }
        }
    }
}