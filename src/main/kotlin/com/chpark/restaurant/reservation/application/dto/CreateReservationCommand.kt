package com.chpark.restaurant.reservation.application.dto

import java.time.Instant

data class CreateReservationCommand(
    val resourceId: String,
    val userId: String,
    val partySize: Int,
    val startAt: Instant,
    val endAt: Instant
)