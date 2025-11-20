package com.chpark.restaurant.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.server.WebFilter
import java.util.*

@Configuration
class WebFluxConfig {

    private val log = LoggerFactory.getLogger(WebFluxConfig::class.java)

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = listOf("http://localhost:3000", "http://localhost:8080")
            allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun loggingFilter(): WebFilter = WebFilter { exchange, chain ->
        val request = exchange.request
        val correlationId = request.headers.getFirst("X-Correlation-Id") ?: UUID.randomUUID().toString()

        val mutatedExchange = exchange.mutate()
            .request(
                request.mutate()
                    .header("X-Correlation-Id", correlationId)
                    .build()
            )
            .build()

        log.info(
            "[{}] {} {}",
            correlationId,
            request.method,
            request.uri
        )

        chain.filter(mutatedExchange)
            .doOnSuccess {
                log.info("[{}] response committed", correlationId)
            }
            .doOnError { ex ->
                log.error("[{}] response error", correlationId, ex)
            }
    }
}