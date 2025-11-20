package com.chpark.restaurant.common.presentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RootRouter {

    @Bean
    fun rootRoutes() = coRouter {
        GET("/health") {
            ServerResponse.ok().bodyValueAndAwait("OK")
        }

        GET("/") {
            ServerResponse.ok().bodyValueAndAwait("Reservation Service (WebFlux + Coroutines + R2DBC)")
        }
    }
}