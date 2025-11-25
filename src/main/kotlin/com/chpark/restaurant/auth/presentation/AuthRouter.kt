package com.chpark.restaurant.auth.presentation

import com.chpark.restaurant.common.router.commonRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON

@Configuration
class AuthRouter(
    private val authHandler: AuthHandler
) {
    @Bean
    fun authRoutes() = commonRoutes {
        "/api/auth".nest {
            POST("/register", accept(APPLICATION_JSON), authHandler::register)
            POST("/login", accept(APPLICATION_JSON), authHandler::login)
            POST("/reissue", accept(APPLICATION_JSON), authHandler::reissue)
            POST("/logout", accept(APPLICATION_JSON), authHandler::logout)
        }
    }
}