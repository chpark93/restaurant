package com.chpark.restaurant.store.presentation

import com.chpark.restaurant.common.router.commonRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StoreRouter(
    private val storeHandler: StoreHandler
) {

    @Bean
    fun storeRoutes() = commonRoutes {
        "/api/stores".nest {
            POST("", storeHandler::registerStore)
            GET("", storeHandler::getStores)
            GET("/{id}", storeHandler::getStore)

            "/{storeId}/items".nest {
                POST("", storeHandler::registerItem)
                GET("", storeHandler::getItemsByStore)
            }
        }
    }
}