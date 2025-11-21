package com.chpark.restaurant.reservation.domain

enum class ReservationStatus {
    REQUESTED,
    CONFIRMED,
    WAITING,
    SEATED,
    CANCELLED,
    NO_SHOW
}