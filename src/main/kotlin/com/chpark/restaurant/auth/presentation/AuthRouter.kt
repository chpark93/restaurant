package com.chpark.restaurant.auth.presentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class AuthRouter(
    private val authHandler: AuthHandler
) {

    @Bean
    fun authRoutes() = coRouter {
        "/api/auth".nest {
            POST("/register", authHandler::register)
            POST("/login", authHandler::login)
            POST("/reissue", authHandler::reissue)
            POST("/logout", authHandler::logout)
        }
    }
}