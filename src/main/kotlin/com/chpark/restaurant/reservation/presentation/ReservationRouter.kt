package com.chpark.restaurant.reservation.presentation

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ReservationRouter {

    @Bean
    fun reservationRoutes(
        reservationHandler: ReservationHandler
    ) = coRouter {
        "/api/reservations".nest {
            POST("", reservationHandler::createReservation)
            GET("/{id}", reservationHandler::getReservation)
            DELETE("/{id}", reservationHandler::cancelReservation)
        }
    }
}