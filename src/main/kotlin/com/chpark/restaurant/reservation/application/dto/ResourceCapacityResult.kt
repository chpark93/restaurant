package com.chpark.restaurant.reservation.application.dto

data class ResourceCapacityResult(
    val resourceId: Long,
    val totalCapacity: Int,
    val reservedCount: Long,
    val availableCapacity: Int
)