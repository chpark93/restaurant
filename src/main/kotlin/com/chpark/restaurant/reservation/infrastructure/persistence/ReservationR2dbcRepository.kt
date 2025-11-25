package com.chpark.restaurant.reservation.infrastructure.persistence

import com.chpark.restaurant.reservation.domain.ReservationStatus
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.Instant

interface ReservationR2dbcRepository : CoroutineCrudRepository<ReservationEntity, Long> {

    fun findAllByResourceIdAndStatusIn(
        resourceId: Long,
        statuses: Collection<ReservationStatus>
    ): Flow<ReservationEntity>

    fun findAllByResourceIdAndStartAtLessThanAndEndAtGreaterThanAndStatusIn(
        resourceId: Long,
        endAt: Instant,
        startAt: Instant,
        statuses: Collection<ReservationStatus>
    ): Flow<ReservationEntity>

    suspend fun findFirstByResourceIdAndStartAtLessThanAndEndAtGreaterThanAndStatusOrderByWaitingNumberAsc(
        resourceId: Long,
        endAt: Instant,
        startAt: Instant,
        status: ReservationStatus = ReservationStatus.WAITING
    ): ReservationEntity?

    suspend fun countByResourceIdAndStartAtLessThanAndEndAtGreaterThanAndStatusIn(
        resourceId: Long,
        end: Instant,
        start: Instant,
        statuses: Collection<ReservationStatus>,
    ): Long
}