package com.chpark.restaurant.reservation.infrastructure.persistence

import com.chpark.restaurant.reservation.domain.reservation.ReservationStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("reservations")
data class ReservationEntity(
    @Id
    val id: Long? = null,
    val resourceId: String,
    val userId: String,
    val partySize: Int,
    val status: ReservationStatus,
    val startAt: Instant,
    val endAt: Instant,
    val waitingNumber: Int? = null,
    @CreatedDate
    val createdAt: Instant? = null,
    @LastModifiedDate
    val updatedAt: Instant? = null
)