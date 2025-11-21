package com.chpark.restaurant.reservation.infrastructure.persistence

import com.chpark.restaurant.reservation.domain.TimeSlot
import com.chpark.restaurant.reservation.domain.Reservation
import com.chpark.restaurant.reservation.domain.ReservationStatus
import com.chpark.restaurant.reservation.domain.port.ReservationRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component

@Component
class ReservationRepositoryAdapter(
    private val reservationR2dbcRepository: ReservationR2dbcRepository
) : ReservationRepository {

    override suspend fun save(
        reservation: Reservation
    ): Reservation {
        val saved = reservationR2dbcRepository.save(
            entity = reservation.toEntity()
        )

        return saved.toDomain()
    }

    override suspend fun findById(
        id: Long
    ): Reservation? = reservationR2dbcRepository.findById(
        id = id
    )?.toDomain()

    override suspend fun findOverlapping(
        resourceId: String,
        slot: TimeSlot
    ): List<Reservation> {
        val activeStatuses = listOf(
            ReservationStatus.REQUESTED,
            ReservationStatus.CONFIRMED,
            ReservationStatus.WAITING,
            ReservationStatus.SEATED
        )

        return reservationR2dbcRepository.findAllByResourceIdAndStatusIn(
            resourceId = resourceId,
            statuses = activeStatuses
        ).map {
            it.toDomain()
        }.filter { existing ->
            existing.timeSlot.overlaps(
                timeSlot = slot
            )
        }.toList()
    }
}