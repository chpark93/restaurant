package com.chpark.restaurant.auth.infrastructure.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(
    "app.jwt"
)
data class JwtProperties(
    val secret: String,
    val issuer: String = "restaurant-service",
    val accessTokenTtl: Long = 1800,
    val refreshTokenTtl: Long = 604800
)