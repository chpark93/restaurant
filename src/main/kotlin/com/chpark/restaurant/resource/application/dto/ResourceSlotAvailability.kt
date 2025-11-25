package com.chpark.restaurant.resource.application.dto

import java.time.Instant

data class ResourceSlotAvailability(
    val resourceId: Long,
    val startAt: Instant,
    val endAt: Instant,
    val capacity: Int,
    val reserved: Int,
    val available: Int,
    val canReserve: Boolean
)