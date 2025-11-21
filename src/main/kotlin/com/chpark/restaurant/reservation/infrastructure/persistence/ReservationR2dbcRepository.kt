package com.chpark.restaurant.reservation.infrastructure.persistence

import com.chpark.restaurant.reservation.domain.ReservationStatus
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ReservationR2dbcRepository : CoroutineCrudRepository<ReservationEntity, Long> {

    fun findAllByResourceIdAndStatusIn(
        resourceId: String,
        statuses: Collection<ReservationStatus>
    ): Flow<ReservationEntity>
}