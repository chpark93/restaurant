package com.chpark.restaurant.config

import com.chpark.restaurant.auth.infrastructure.jwt.JwtAuthenticationWebFilter
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.common.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtAuthenticationWebFilter: JwtAuthenticationWebFilter,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain =
        http
            .csrf {
                it.disable()
            }
            .httpBasic {
                it.disable()
            }
            .formLogin {
                it.disable()
            }
            .logout {
                it.disable()
            }
            .authorizeExchange { authorizeExchangeSpec ->
                authorizeExchangeSpec.pathMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/api/auth/register",
                    "/api/auth/login"
                ).permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .exceptionHandling { exceptionHandlingSpec ->
                exceptionHandlingSpec.authenticationEntryPoint { serverWebExchange, _ ->
                    val response = serverWebExchange.response
                    response.headers.contentType = MediaType.APPLICATION_JSON
                    response.statusCode = HttpStatus.UNAUTHORIZED

                    val body = ApiResponse.fail(
                        code = ErrorCode.UNAUTHORIZED.code,
                        message = ErrorCode.UNAUTHORIZED.message
                    )
                    val bytes = objectMapper.writeValueAsBytes(body)
                    val buffer = response.bufferFactory().wrap(bytes)

                    response.writeWith(Mono.just(buffer))
                }

                exceptionHandlingSpec.accessDeniedHandler { serverWebExchange, _ ->
                    val response = serverWebExchange.response
                    response.headers.contentType = MediaType.APPLICATION_JSON
                    response.statusCode = HttpStatus.FORBIDDEN

                    val body = ApiResponse.fail(
                        code = ErrorCode.FORBIDDEN.code,
                        message = ErrorCode.FORBIDDEN.message
                    )
                    val bytes = objectMapper.writeValueAsBytes(body)
                    val buffer = response.bufferFactory().wrap(bytes)

                    response.writeWith(Mono.just(buffer))
                }
            }
            .build()
}