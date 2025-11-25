// src/test/kotlin/com/chpark/restaurant/support/TestSecurityConfig.kt
package com.chpark.restaurant.support

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@TestConfiguration
class TestSecurityConfig {

    @Bean
    @Order(0)
    fun testSecurityWebFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain =
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .authorizeExchange {
                it.anyExchange().permitAll()
            }
            .build()
}