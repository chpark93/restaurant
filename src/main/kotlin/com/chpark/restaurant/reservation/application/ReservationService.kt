package com.chpark.restaurant.reservation.application

import com.chpark.restaurant.reservation.application.dto.CreateReservationCommand
import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.reservation.domain.TimeSlot
import com.chpark.restaurant.reservation.domain.Reservation
import com.chpark.restaurant.reservation.domain.ReservationStatus
import com.chpark.restaurant.reservation.domain.port.ReservationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository
) {

    @Transactional
    suspend fun createReservation(
        command: CreateReservationCommand
    ): Reservation {
        val timeSlot = TimeSlot(
            start = command.startAt,
            end = command.endAt
        )

        val overlapping = reservationRepository.findOverlapping(
            resourceId = command.resourceId,
            timeSlot = timeSlot
        )

        val (status, waitingNumber) = if (overlapping.isEmpty()) {
            ReservationStatus.CONFIRMED to null
        } else {
            val waitingCount = overlapping.count { it.status == ReservationStatus.WAITING }
            ReservationStatus.WAITING to (waitingCount + 1)
        }

        val reservation = Reservation(
            resourceId = command.resourceId,
            userId = command.userId,
            partySize = command.partySize,
            timeSlot = timeSlot,
            status = status,
            waitingNumber = waitingNumber
        )

        return reservationRepository.save(
            reservation = reservation
        )
    }

    @Transactional(readOnly = true)
    suspend fun getReservation(
        id: Long
    ): Reservation = reservationRepository.findById(
        id = id
    ) ?: throw BusinessException(ErrorCode.RESERVATION_NOT_FOUND)

    @Transactional
    suspend fun cancelReservation(
        id: Long,
        userId: String
    ) {
        val existedReservation = reservationRepository.findById(
            id = id
        ) ?: throw BusinessException(ErrorCode.RESERVATION_NOT_FOUND)

        if (existedReservation.userId != userId) {
            throw BusinessException(ErrorCode.RESERVATION_ACCESS_DENIED)
        }

        if (existedReservation.status == ReservationStatus.CANCELLED ||
            existedReservation.status == ReservationStatus.NO_SHOW
        ) {
            return
        }

        val cancelledReservation = existedReservation.copy(
            status = ReservationStatus.CANCELLED
        )

        reservationRepository.save(
            reservation = cancelledReservation
        )
    }
}