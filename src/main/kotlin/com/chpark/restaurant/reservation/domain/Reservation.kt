package com.chpark.restaurant.reservation.domain

data class Reservation(
    val id: Long? = null,
    val resourceId: String,
    val userId: String,
    val partySize: Int,
    val timeSlot: TimeSlot,
    val status: ReservationStatus,
    val waitingNumber: Int? = null
) {
    fun isActive(): Boolean =
        status in setOf(
            ReservationStatus.REQUESTED,
            ReservationStatus.CONFIRMED,
            ReservationStatus.WAITING,
            ReservationStatus.SEATED
        )
}