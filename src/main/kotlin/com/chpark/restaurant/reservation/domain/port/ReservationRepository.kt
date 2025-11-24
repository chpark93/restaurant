package com.chpark.restaurant.reservation.domain.port

import com.chpark.restaurant.reservation.domain.Reservation
import com.chpark.restaurant.reservation.domain.TimeSlot

interface ReservationRepository {

    suspend fun save(
        reservation: Reservation
    ): Reservation

    suspend fun findById(
        id: Long
    ): Reservation?

    suspend fun findOverlapping(
        resourceId: String,
        timeSlot: TimeSlot
    ): List<Reservation>

    suspend fun findNextWaiting(
        resourceId: String,
        timeSlot: TimeSlot
    ): Reservation?
}