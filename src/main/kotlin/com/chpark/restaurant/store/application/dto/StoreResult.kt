package com.chpark.restaurant.store.application.dto

data class StoreResult(
    val id: Long,
    val code: String,
    val name: String,
    val description: String?
)