package com.chpark.restaurant.reservation.domain

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import java.time.Instant

data class TimeSlot(
    val start: Instant,
    val end: Instant
) {
    init {
        require(end.isAfter(start)) {
            throw BusinessException(ErrorCode.RESERVATION_INVALID_TIME_SLOT)
        }
    }

    fun overlaps(
        timeSlot: TimeSlot
    ): Boolean =
        this.start < timeSlot.end && this.end > timeSlot.start
}