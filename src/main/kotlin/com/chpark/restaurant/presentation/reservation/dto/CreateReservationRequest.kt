package com.chpark.restaurant.presentation.reservation.dto

import com.chpark.restaurant.reservation.application.dto.CreateReservationCommand
import com.chpark.restaurant.reservation.domain.reservation.Reservation
import com.chpark.restaurant.reservation.domain.reservation.ReservationStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.Instant

object ReservationDtos {
    data class CreateReservationRequest(
        @field:NotBlank
        val resourceId: String,
        @field:NotBlank
        val userId: String,
        @field:Min(1)
        val partySize: Int,
        @field:NotNull
        val startAt: Instant,
        @field:NotNull
        val endAt: Instant
    ) {
        fun toCommand(): CreateReservationCommand = CreateReservationCommand(
            resourceId = resourceId,
            userId = userId,
            partySize = partySize,
            startAt = startAt,
            endAt = endAt
        )
    }

    data class ReservationResponse(
        val id: Long?,
        val resourceId: String,
        val userId: String,
        val partySize: Int,
        val status: ReservationStatus,
        val startAt: Instant,
        val endAt: Instant,
        val waitingNumber: Int?
    ) {
        companion object {
            fun from(
                reservation: Reservation
            ): ReservationResponse = ReservationResponse(
                id = reservation.id,
                resourceId = reservation.resourceId,
                userId = reservation.userId,
                partySize = reservation.partySize,
                status = reservation.status,
                startAt = reservation.timeSlot.start,
                endAt = reservation.timeSlot.end,
                waitingNumber = reservation.waitingNumber
            )
        }
    }
}