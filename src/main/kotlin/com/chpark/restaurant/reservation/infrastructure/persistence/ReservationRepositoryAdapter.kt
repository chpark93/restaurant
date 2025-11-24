package com.chpark.restaurant.reservation.infrastructure.persistence

import com.chpark.restaurant.reservation.domain.Reservation
import com.chpark.restaurant.reservation.domain.ReservationStatus
import com.chpark.restaurant.reservation.domain.TimeSlot
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
        val savedReservation = reservationR2dbcRepository.save(
            ReservationMapper.toEntity(
                domain = reservation
            )
        )

        return ReservationMapper.toDomain(
            entity = savedReservation
        )
    }

    override suspend fun findById(
        id: Long
    ): Reservation? = reservationR2dbcRepository.findById(
        id = id
    )?.let { entity ->
        ReservationMapper.toDomain(
            entity = entity
        )
    }

    override suspend fun findOverlapping(
        resourceId: String,
        timeSlot: TimeSlot
    ): List<Reservation> {
        val activeStatuses = listOf(
            ReservationStatus.REQUESTED,
            ReservationStatus.CONFIRMED,
            ReservationStatus.WAITING,
            ReservationStatus.SEATED
        )

        return reservationR2dbcRepository.findAllByResourceIdAndStartAtLessThanAndEndAtGreaterThanAndStatusIn(
            resourceId = resourceId,
            endAt = timeSlot.end,
            startAt = timeSlot.start,
            statuses = activeStatuses
        ).map { entity ->
            ReservationMapper.toDomain(
                entity = entity
            )
        }.filter { existing ->
            existing.timeSlot.overlaps(
                timeSlot = timeSlot
            )
        }.toList()
    }

    override suspend fun findNextWaiting(
        resourceId: String,
        timeSlot: TimeSlot
    ): Reservation? {
        reservationR2dbcRepository.findFirstByResourceIdAndStartAtLessThanAndEndAtGreaterThanAndStatusOrderByWaitingNumberAsc(
            resourceId = resourceId,
            endAt = timeSlot.end,
            startAt = timeSlot.start,
            status = ReservationStatus.WAITING
        ).let { reservation ->
            return reservation?.let {
                ReservationMapper.toDomain(
                    entity = it
                )
            }
        }
    }
}