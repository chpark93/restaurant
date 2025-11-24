package com.chpark.restaurant.resource.infrastructure.persistence

import com.chpark.restaurant.resource.domain.Resource
import com.chpark.restaurant.resource.domain.port.ResourceRepository
import org.springframework.stereotype.Component

@Component
class ResourceRepositoryAdapter(
    private val resourceR2dbcRepository: ResourceR2dbcRepository
) : ResourceRepository {

    override suspend fun findByCode(
        code: String
    ): Resource? = resourceR2dbcRepository.findByCode(
        code = code
    )?.let { entity ->
        ResourceMapper.toDomain(
            entity = entity
        )
    }

    override suspend fun existsByCode(
        code: String
    ): Boolean = resourceR2dbcRepository.existsByCode(
        code = code
    )

    override suspend fun save(
        resource: Resource
    ): Resource {
        val savedResource = resourceR2dbcRepository.save(
            entity = ResourceMapper.toEntity(
                domain = resource
            )
        )
        return ResourceMapper.toDomain(
            entity = savedResource
        )
    }
}