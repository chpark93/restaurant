package com.chpark.restaurant.resource.presentation

import com.chpark.restaurant.common.router.commonRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class ResourceRouter(
    private val resourceHandler: ResourceHandler
) {

    @Bean
    fun resourceRoutes(): RouterFunction<ServerResponse> = commonRoutes {
        "/api/resources".nest {
            POST("", resourceHandler::createResource)
            GET("/{code}", resourceHandler::getResource)
        }
    }
}