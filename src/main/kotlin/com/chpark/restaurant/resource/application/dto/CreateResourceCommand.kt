package com.chpark.restaurant.resource.application.dto

import com.chpark.restaurant.resource.domain.ResourceType

data class CreateResourceCommand(
    val code: String,
    val name: String,
    val capacity: Int,
    val type: ResourceType
)