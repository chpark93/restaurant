package com.chpark.restaurant.reservation.domain

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode

class Reservation private constructor(
    val id: Long? = null,
    val resourceId: String,
    val userId: String,
    val partySize: Int,
    val timeSlot: TimeSlot,
    private var status: ReservationStatus,
    val waitingNumber: Int? = null
) {
    companion object {
        fun create(
            resourceId: String,
            userId: String,
            partySize: Int,
            timeSlot: TimeSlot,
            status: ReservationStatus,
            waitingNumber: Int? = null
        ): Reservation = Reservation(
            resourceId = resourceId,
            userId = userId,
            partySize = partySize,
            timeSlot = timeSlot,
            status = status,
            waitingNumber = waitingNumber
        )

        fun reConstruct(
            id: Long?,
            resourceId: String,
            userId: String,
            partySize: Int,
            timeSlot: TimeSlot,
            status: ReservationStatus,
            waitingNumber: Int? = null
        ): Reservation = Reservation(
            id = id,
            resourceId = resourceId,
            userId = userId,
            partySize = partySize,
            timeSlot = timeSlot,
            status = status,
            waitingNumber = waitingNumber
        )
    }

    fun status(): ReservationStatus = status

    fun isWaiting(): Boolean = status == ReservationStatus.WAITING

    fun isActive(): Boolean = status in setOf(
        ReservationStatus.REQUESTED,
        ReservationStatus.CONFIRMED,
        ReservationStatus.WAITING,
        ReservationStatus.SEATED
    )

    fun cancel() {
        if (isTerminal()) {
            return
        }

        changeStatus(
            status = ReservationStatus.CANCELLED
        )
    }

    fun isTerminal(): Boolean = status in setOf(
        ReservationStatus.CANCELLED,
        ReservationStatus.NO_SHOW
    )

    fun changeStatus(
        status: ReservationStatus
    ) {
        this.status = status
    }

    fun changeConfirmedFromWaiting() {
        require(isWaiting()) {
            throw BusinessException(ErrorCode.RESERVATION_ONLY_WAITING_CAN_BE_CONFIRMED)
        }

        changeStatus(
            status = ReservationStatus.CONFIRMED
        )
    }
}