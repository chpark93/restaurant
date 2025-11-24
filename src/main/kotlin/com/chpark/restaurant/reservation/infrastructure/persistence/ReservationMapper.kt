package com.chpark.restaurant.reservation.infrastructure.persistence

import com.chpark.restaurant.reservation.domain.Reservation
import com.chpark.restaurant.reservation.domain.TimeSlot

object ReservationMapper {

    fun toEntity(
        domain: Reservation,
    ): ReservationEntity = ReservationEntity(
        id = domain.id,
        resourceId = domain.resourceId,
        userId = domain.userId,
        partySize = domain.partySize,
        status = domain.status(),
        startAt = domain.timeSlot.start,
        endAt = domain.timeSlot.end,
        waitingNumber = domain.waitingNumber
    )

    fun toDomain(
        entity: ReservationEntity,
    ): Reservation = Reservation.reConstruct(
        id = entity.id,
        resourceId = entity.resourceId,
        userId = entity.userId,
        partySize = entity.partySize,
        timeSlot = TimeSlot(
            start = entity.startAt,
            end = entity.endAt
        ),
        status = entity.status,
        waitingNumber = entity.waitingNumber
    )
}