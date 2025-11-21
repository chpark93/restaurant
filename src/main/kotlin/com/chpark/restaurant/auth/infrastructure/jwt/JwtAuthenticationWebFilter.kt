package com.chpark.restaurant.auth.infrastructure.jwt

import com.chpark.restaurant.auth.domain.port.AuthTokenParser
import com.chpark.restaurant.auth.domain.port.TokenBlacklistStore
import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationWebFilter(
    private val tokenParser: AuthTokenParser,
    private val tokenBlacklistStore: TokenBlacklistStore,
    private val objectMapper: ObjectMapper
) : WebFilter {

    private val publicPrefixes = listOf(
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/api/auth/register",
        "/api/auth/login"
    )

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain
    ): Mono<Void> {
        val path = exchange.request.path.pathWithinApplication().value()
        if (isPublicPath(path = path)) {
            return chain.filter(exchange)
        }

        val token = resolveToken(
            exchange = exchange
        ) ?: return chain.filter(exchange)

        return mono {
            val isBlacklisted = tokenBlacklistStore.isBlacklisted(
                accessToken = token
            )
            if (isBlacklisted) throw BusinessException(ErrorCode.TOKEN_INVALID)

            tokenParser.parse(
                token = token
            )
        }.flatMap { claims ->
            val authorities = claims.roles.map {
                SimpleGrantedAuthority("ROLE_$it")
            }
            val auth = UsernamePasswordAuthenticationToken(
                claims.subject,
                null,
                authorities
            )
            val context = SecurityContextImpl(auth)

            chain.filter(exchange).contextWrite(
                ReactiveSecurityContextHolder.withSecurityContext(
                    Mono.just(context)
                )
            )
        }
    }

    private fun resolveToken(
        exchange: ServerWebExchange
    ): String? {
        val bearer = exchange.request.headers.getFirst("Authorization") ?: return null
        if (!bearer.startsWith("Bearer ", ignoreCase = true)) return null

        return bearer.substring("Bearer ".length).trim()
    }

    private fun isPublicPath(
        path: String
    ): Boolean = publicPrefixes.any { prefix ->
        path.startsWith(prefix)
    }
}