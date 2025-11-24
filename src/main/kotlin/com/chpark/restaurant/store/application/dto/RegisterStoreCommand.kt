package com.chpark.restaurant.store.application.dto

data class RegisterStoreCommand(
    val code: String,
    val name: String,
    val description: String?
)