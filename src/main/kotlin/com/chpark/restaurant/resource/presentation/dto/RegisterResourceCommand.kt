package com.chpark.restaurant.resource.presentation.dto

import com.chpark.restaurant.resource.domain.ResourceType

data class RegisterResourceCommand(
    val storeId: Long,
    val code: String,
    val name: String,
    val capacity: Int,
    val type: ResourceType
)