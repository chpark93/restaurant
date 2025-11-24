package com.chpark.restaurant.reservation.presentation

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.common.response.ApiResponse
import com.chpark.restaurant.reservation.application.ReservationService
import com.chpark.restaurant.reservation.presentation.dto.ReservationDtos
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
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
        val userId = extractUserId(
            request = request
        )
        val body = request.awaitBody<ReservationDtos.CreateReservationRequest>()
        val command = body.toCommand(
            userId = userId
        )

        val reservationId = reservationService.createReservation(
            command = command
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = ReservationDtos.ReservationIdResponse(
                        id = reservationId
                    )
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

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = reservation.let {
                        ReservationDtos.ReservationResponse.from(
                            reservation = it
                        )
                    }
                )
            )
    }

    suspend fun cancelReservation(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val userId = extractUserId(
            request = request
        )

        reservationService.cancelReservation(
            id = id,
            userId = userId
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok()
            )
    }

    private suspend fun extractUserId(
        request: ServerRequest
    ): String {
        val principal = request.principal().awaitSingle()
        val authentication = principal as? Authentication
            ?: throw BusinessException(ErrorCode.UNAUTHORIZED)

        return authentication.name
    }
}
