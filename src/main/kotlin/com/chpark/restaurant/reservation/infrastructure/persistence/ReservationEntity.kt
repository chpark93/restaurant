package com.chpark.restaurant.reservation.infrastructure.persistence

import com.chpark.restaurant.reservation.domain.ReservationStatus
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("reservations")
data class ReservationEntity(
    @Id
    val id: Long? = null,

    @Column("resource_id")
    val resourceId: Long,

    @Column("user_id")
    val userId: String,

    @Column("party_size")
    val partySize: Int,

    @Column("status")
    val status: ReservationStatus,

    @Column("start_at")
    val startAt: Instant,

    @Column("end_at")
    val endAt: Instant,

    @Column("waiting_number")
    val waitingNumber: Int? = null,

    @CreatedDate
    @Column("created_at")
    val createdAt: Instant? = null,

    @LastModifiedDate
    @Column("updated_at")
    val updatedAt: Instant? = null
)