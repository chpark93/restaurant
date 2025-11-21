package com.chpark.restaurant.auth.presentation

import com.chpark.restaurant.common.router.commonRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AuthRouter(
    private val authHandler: AuthHandler
) {
    @Bean
    fun authRoutes() = commonRoutes {
        "/api/auth".nest {
            POST("/register", authHandler::register)
            POST("/login", authHandler::login)
            POST("/reissue", authHandler::reissue)
            POST("/logout", authHandler::logout)
        }
    }
}