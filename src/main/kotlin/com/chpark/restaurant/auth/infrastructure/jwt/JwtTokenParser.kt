package com.chpark.restaurant.auth.infrastructure.jwt

import com.chpark.restaurant.auth.domain.AuthTokenClaims
import com.chpark.restaurant.auth.domain.port.AuthTokenParser
import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class JwtTokenParser(
    private val jwtProperties: JwtProperties
) : AuthTokenParser {

    private val secretKey: SecretKey =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret))

    override fun parse(
        token: String
    ): AuthTokenClaims =
        try {
            val jwt = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)

            val body = jwt.payload
            val subject = body.subject ?: throw BusinessException(ErrorCode.TOKEN_EMPTY)

            val issuer = body.issuer
            if (issuer != jwtProperties.issuer) {
                throw BusinessException(ErrorCode.TOKEN_INVALID)
            }

            val roles = (body["roles"] as? List<*>)?.map { it.toString() } ?: emptyList()
            val type = body["type"]?.toString() ?: ""

            val tokenType = runCatching {
                AuthTokenClaims.TokenType.valueOf(
                    value = type
                )
            }.getOrElse {
                throw BusinessException(ErrorCode.TOKEN_INVALID)
            }

            AuthTokenClaims(
                subject = subject,
                roles = roles,
                tokenType = tokenType
            )
        } catch (exception: ExpiredJwtException) {
            throw BusinessException(ErrorCode.TOKEN_EXPIRED)
        } catch (exception: JwtException) {
            throw BusinessException(ErrorCode.TOKEN_INVALID)
        }
}