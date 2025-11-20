package com.chpark.restaurant.reservation.presentation

import com.chpark.restaurant.reservation.application.ReservationService
import com.chpark.restaurant.reservation.application.dto.CreateReservationCommand
import com.chpark.restaurant.common.response.ApiResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class ReservationHandler(
    private val reservationService: ReservationService
) {

    suspend fun createReservation(
        request: ServerRequest
    ): ServerResponse {
        val command: CreateReservationCommand = request.awaitBody()

        val reservation = reservationService.createReservation(
            command = command
        )

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = reservation
                )
            )
    }

    suspend fun getReservation(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val reservation = reservationService.getReservation(
            id = id
        )

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = reservation
                )
            )
    }

    suspend fun cancelReservation(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val userId = request.queryParam("userId").orElseThrow {
            TODO("add authentication and get userId")
        }

        reservationService.cancelReservation(
            id = id,
            userId = userId
        )

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok()
            )
    }
}
