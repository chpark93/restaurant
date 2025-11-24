package com.chpark.restaurant.reservation.infrastructure.persistence

import com.chpark.restaurant.reservation.domain.ReservationStatus
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.Instant

interface ReservationR2dbcRepository : CoroutineCrudRepository<ReservationEntity, Long> {

    suspend fun findAllByResourceIdAndStatusIn(
        resourceId: String,
        statuses: Collection<ReservationStatus>
    ): Flow<ReservationEntity>

    suspend fun findAllByResourceIdAndStartAtLessThanAndEndAtGreaterThanAndStatusIn(
        resourceId: String,
        endAt: Instant,
        startAt: Instant,
        statuses: Collection<ReservationStatus>
    ): Flow<ReservationEntity>

    suspend fun findFirstByResourceIdAndStartAtLessThanAndEndAtGreaterThanAndStatusOrderByWaitingNumberAsc(
        resourceId: String,
        endAt: Instant,
        startAt: Instant,
        status: ReservationStatus = ReservationStatus.WAITING
    ): ReservationEntity?
}