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

    companion object {
        private val ACTIVE_STATUSES = setOf(
            ReservationStatus.REQUESTED,
            ReservationStatus.CONFIRMED,
            ReservationStatus.WAITING,
            ReservationStatus.SEATED
        )
    }

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
        resourceId: Long,
        timeSlot: TimeSlot
    ): List<Reservation> {
        val activeStatuses = activeStatuses()

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
        resourceId: Long,
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

    override suspend fun countActiveOverlapping(
        resourceId: Long,
        timeSlot: TimeSlot
    ): Long = reservationR2dbcRepository.countByResourceIdAndStartAtLessThanAndEndAtGreaterThanAndStatusIn(
        resourceId = resourceId,
        statuses = ACTIVE_STATUSES,
        end = timeSlot.end,
        start = timeSlot.start
    )

    private fun activeStatuses(): List<ReservationStatus> =
        listOf(
            ReservationStatus.REQUESTED,
            ReservationStatus.CONFIRMED,
            ReservationStatus.WAITING,
            ReservationStatus.SEATED
        )
}