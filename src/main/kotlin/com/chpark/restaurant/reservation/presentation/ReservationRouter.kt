package com.chpark.restaurant.reservation.presentation

import com.chpark.restaurant.common.router.commonRoutes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ReservationRouter {

    @Bean
    fun reservationRoutes(
        reservationHandler: ReservationHandler
    ) = commonRoutes {
        "/api/reservations".nest {
            POST("", reservationHandler::createReservation)
            GET("/{id}", reservationHandler::getReservation)
            DELETE("/{id}", reservationHandler::cancelReservation)
        }
    }
}