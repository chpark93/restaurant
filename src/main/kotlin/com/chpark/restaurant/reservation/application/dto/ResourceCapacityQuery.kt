package com.chpark.restaurant.reservation.application.dto

import java.time.Instant

data class ResourceCapacityQuery(
    val resourceId: Long,
    val startAt: Instant,
    val endAt: Instant
)