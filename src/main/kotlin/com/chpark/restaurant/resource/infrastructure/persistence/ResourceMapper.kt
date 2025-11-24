package com.chpark.restaurant.resource.infrastructure.persistence

import com.chpark.restaurant.resource.domain.Resource
import com.chpark.restaurant.resource.domain.ResourceType

object ResourceMapper {

    fun toEntity(
        domain: Resource
    ): ResourceEntity = ResourceEntity(
        id = domain.id,
        code = domain.code,
        name = domain.name,
        capacity = domain.capacity,
        active = domain.isActive(),
        type = domain.type.name
    )

    fun toDomain(
        entity: ResourceEntity
    ): Resource = Resource.reConstruct(
        id = entity.id,
        code = entity.code,
        name = entity.name,
        capacity = entity.capacity,
        active = entity.active,
        type = ResourceType.valueOf(entity.type)
    )
}