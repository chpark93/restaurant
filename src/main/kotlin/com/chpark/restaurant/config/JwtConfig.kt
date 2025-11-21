package com.chpark.restaurant.config

import com.chpark.restaurant.auth.infrastructure.jwt.JwtProperties
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.crypto.SecretKey

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtConfig(
    private val jwtProperties: JwtProperties
) {

    @Bean
    fun jwtSigningKey(): SecretKey =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret))

    @Bean
    fun jwtParser(
        secretKey: SecretKey
    ): JwtParser = Jwts.parser()
        .verifyWith(secretKey)
        .requireIssuer(jwtProperties.issuer)
        .build()
}