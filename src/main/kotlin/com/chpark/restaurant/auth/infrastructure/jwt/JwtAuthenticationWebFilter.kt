package com.chpark.restaurant.auth.infrastructure.jwt

import com.chpark.restaurant.auth.domain.port.AuthTokenParser
import com.chpark.restaurant.auth.domain.port.TokenBlacklistStore
import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationWebFilter(
    private val tokenParser: AuthTokenParser,
    private val tokenBlacklistStore: TokenBlacklistStore
) : WebFilter {

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain
    ): Mono<Void> {
        val request = exchange.request
        val bearer = request.headers.getFirst("Authorization") ?: return chain.filter(exchange)

        if (!bearer.startsWith("Bearer ", ignoreCase = true)) {
            return chain.filter(exchange)
        }

        val token = bearer.substring("Bearer ".length).trim()

        return mono {
            val isBlacklisted = tokenBlacklistStore.isBlacklisted(token)
            if (isBlacklisted) {
                null
            } else {
                try {
                    val claims = tokenParser.parse(token)
                    val authorities = claims.roles.map { SimpleGrantedAuthority("ROLE_$it") }
                    val auth = UsernamePasswordAuthenticationToken(
                        claims.subject,
                        null,
                        authorities
                    )
                    SecurityContextImpl(auth)
                } catch (exception: BusinessException) {
                    if (exception.errorCode in setOf(
                            ErrorCode.TOKEN_EXPIRED,
                            ErrorCode.TOKEN_INVALID,
                            ErrorCode.INVALID_REFRESH_TOKEN
                        )
                    ) {
                        null
                    } else {
                        throw exception
                    }
                }
            }
        }.flatMap { context ->
            if (context == null) {
                chain.filter(exchange)
            } else {
                chain.filter(exchange)
                    .contextWrite(
                        ReactiveSecurityContextHolder.withSecurityContext(
                            Mono.just(context)
                        )
                    )
            }
        }
    }
}