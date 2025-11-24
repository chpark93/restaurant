package com.chpark.restaurant.resource.presentation.dto

import com.chpark.restaurant.resource.application.dto.CreateResourceCommand
import com.chpark.restaurant.resource.domain.Resource
import com.chpark.restaurant.resource.domain.ResourceType

object ResourceDtos {
    data class CreateResourceRequest(
        val code: String,
        val name: String,
        val capacity: Int,
        val type: ResourceType
    )

    data class ResourceResponse(
        val id: Long,
        val code: String,
        val name: String,
        val capacity: Int,
        val active: Boolean,
        val type: ResourceType
    )

    fun CreateResourceRequest.toCommand(): CreateResourceCommand =
        CreateResourceCommand(
            code = code,
            name = name,
            capacity = capacity,
            type = type
        )

    fun Resource.toResponse(): ResourceResponse =
        ResourceResponse(
            id = requireNotNull(id),
            code = code,
            name = name,
            capacity = capacity,
            active = isActive(),
            type = type
        )
}