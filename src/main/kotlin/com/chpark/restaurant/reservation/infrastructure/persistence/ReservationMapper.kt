package com.chpark.restaurant.reservation.infrastructure.persistence

import com.chpark.restaurant.reservation.domain.TimeSlot
import com.chpark.restaurant.reservation.domain.Reservation

fun ReservationEntity.toDomain(): Reservation = Reservation(
    id = id,
    resourceId = resourceId,
    userId = userId,
    partySize = partySize,
    timeSlot = TimeSlot(
        start = startAt,
        end = endAt
    ),
    status = status,
    waitingNumber = waitingNumber
)

fun Reservation.toEntity(): ReservationEntity = ReservationEntity(
    id = id,
    resourceId = resourceId,
    userId = userId,
    partySize = partySize,
    status = status,
    startAt = timeSlot.start,
    endAt = timeSlot.end,
    waitingNumber = waitingNumber
)