package com.chpark.restaurant.reservation.domain.reservation.port

import com.chpark.restaurant.reservation.domain.TimeSlot
import com.chpark.restaurant.reservation.domain.reservation.Reservation

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
}