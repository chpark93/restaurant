package com.chpark.restaurant.auth.application

import com.chpark.restaurant.auth.application.dto.TokenResult
import com.chpark.restaurant.auth.domain.AuthTokenClaims
import com.chpark.restaurant.auth.domain.port.AuthTokenIssuer
import com.chpark.restaurant.auth.domain.port.AuthTokenParser
import com.chpark.restaurant.auth.domain.port.RefreshTokenStore
import com.chpark.restaurant.auth.domain.port.TokenBlacklistStore
import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TokenService(
    private val tokenIssuer: AuthTokenIssuer,
    private val tokenParser: AuthTokenParser,
    private val refreshTokenStore: RefreshTokenStore,
    private val tokenBlacklistStore: TokenBlacklistStore
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    suspend fun issueToken(
        subject: String,
        roles: List<String>
    ): TokenResult {
        val accessToken = tokenIssuer.issueAccessToken(
            subject = subject,
            roles = roles
        )

        val refreshToken = tokenIssuer.issueRefreshToken(
            subject = subject,
            roles = roles
        )

        refreshTokenStore.save(
            userId = subject,
            refreshToken = refreshToken
        )

        return TokenResult(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    @Transactional
    suspend fun reissueToken(
        refreshToken: String
    ): TokenResult {
        val claims = tokenParser.parse(
            token = refreshToken
        )

        if (claims.tokenType != AuthTokenClaims.TokenType.REFRESH_TOKEN) {
            throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val exists = refreshTokenStore.exists(
            userId = claims.subject,
            refreshToken = refreshToken
        )

        if (!exists) {
            throw BusinessException(ErrorCode.INVALID_REFRESH_TOKEN)
        }

        val newAccessToken = tokenIssuer.issueAccessToken(
            subject = claims.subject,
            roles = claims.roles
        )

        val newRefreshToken = tokenIssuer.issueRefreshToken(
            subject = claims.subject,
            roles = claims.roles
        )

        refreshTokenStore.delete(
            userId = claims.subject,
            refreshToken = refreshToken
        )

        refreshTokenStore.save(
            userId = claims.subject,
            refreshToken = newRefreshToken
        )

        return TokenResult(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    suspend fun logout(
        accessToken: String?,
        refreshToken: String?
    ) {
        if (!accessToken.isNullOrBlank()) {
            runCatching {
                tokenBlacklistStore.blacklist(
                    accessToken = accessToken
                )
            }.onFailure {
                logger.warn("failed to blacklist access token", it)
            }
        }

        if (!refreshToken.isNullOrBlank()) {
            runCatching {
                val claims = tokenParser.parse(
                    token = refreshToken
                )

                refreshTokenStore.delete(
                    userId = claims.subject,
                    refreshToken = refreshToken
                )
            }.onFailure {
                logger.warn("failed to delete refresh token", it)
            }
        }
    }
}