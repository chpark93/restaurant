package com.chpark.restaurant.reservation.application

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.reservation.application.dto.CreateReservationCommand
import com.chpark.restaurant.reservation.domain.Reservation
import com.chpark.restaurant.reservation.domain.ReservationStatus
import com.chpark.restaurant.reservation.domain.TimeSlot
import com.chpark.restaurant.reservation.domain.port.ReservationRepository
import com.chpark.restaurant.resource.domain.port.ResourceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val resourceRepository: ResourceRepository
) {

    @Transactional
    suspend fun createReservation(
        command: CreateReservationCommand
    ): Long {
        val timeSlot = TimeSlot(
            start = command.startAt,
            end = command.endAt
        )

        val resource = resourceRepository.findByCode(
            code = command.resourceId
        ) ?: throw BusinessException(ErrorCode.RESOURCE_NOT_FOUND)

        if (!resource.isActive()) {
            throw BusinessException(ErrorCode.RESOURCE_INACTIVE)
        }

        val overlapping = reservationRepository.findOverlapping(
            resourceId = command.resourceId,
            timeSlot = timeSlot
        )

        val inUseCount = overlapping.count { reservation ->
            reservation.isActive() && !reservation.isWaiting()
        }

        val (status, waitingNumber) = if (inUseCount < resource.capacity) {
            ReservationStatus.CONFIRMED to null
        } else {
            val waitingCount = overlapping.count { it.isWaiting() }
            ReservationStatus.WAITING to (waitingCount + 1)
        }

        val reservation = Reservation.create(
            resourceId = command.resourceId,
            userId = command.userId,
            partySize = command.partySize,
            timeSlot = timeSlot,
            status = status,
            waitingNumber = waitingNumber
        )

        val savedReservation = reservationRepository.save(
            reservation = reservation
        )

        return savedReservation.id!!
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

        if (!existedReservation.isActive()) {
            return
        }

        val timeSlot = existedReservation.timeSlot
        val resourceId = existedReservation.resourceId
        val confirmedOrSeated = existedReservation.status() in setOf(
            ReservationStatus.CONFIRMED,
            ReservationStatus.SEATED
        )

        existedReservation.cancel()
        reservationRepository.save(
            reservation = existedReservation
        )

        if (confirmedOrSeated) {
            val nextWaiting = reservationRepository.findNextWaiting(
                resourceId = resourceId,
                timeSlot = timeSlot
            ) ?: return

            nextWaiting.changeConfirmedFromWaiting()
            reservationRepository.save(
                reservation = nextWaiting
            )
        }
    }
}