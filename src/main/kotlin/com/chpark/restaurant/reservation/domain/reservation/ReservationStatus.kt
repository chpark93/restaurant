package com.chpark.restaurant.reservation.domain.reservation

enum class ReservationStatus {
    REQUESTED,
    CONFIRMED,
    WAITING,
    SEATED,
    CANCELLED,
    NO_SHOW
}