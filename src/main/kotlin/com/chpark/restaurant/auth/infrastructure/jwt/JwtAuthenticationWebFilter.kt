package com.chpark.restaurant.auth.infrastructure.jwt

import com.chpark.restaurant.auth.domain.port.AuthTokenParser
import com.chpark.restaurant.auth.domain.port.TokenBlacklistStore
import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
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
    private val tokenBlacklistStore: TokenBlacklistStore
) : WebFilter {

    companion object {
        private const val ROLE_PREFIX = "ROLE_"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private val publicPrefixes = listOf(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api/auth/register",
            "/api/auth/login"
        )
    }

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
                SimpleGrantedAuthority("$ROLE_PREFIX$it")
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
        val bearer = exchange.request.headers.getFirst(AUTHORIZATION_HEADER) ?: return null
        if (!bearer.startsWith(prefix = BEARER_PREFIX, ignoreCase = true)) return null

        return bearer.substring(startIndex = BEARER_PREFIX.length).trim()
    }

    private fun isPublicPath(
        path: String
    ): Boolean = publicPrefixes.any { prefix ->
        path.startsWith(prefix)
    }
}