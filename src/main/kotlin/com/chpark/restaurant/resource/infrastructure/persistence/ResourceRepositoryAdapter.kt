package com.chpark.restaurant.resource.infrastructure.persistence

import com.chpark.restaurant.resource.domain.Resource
import com.chpark.restaurant.resource.domain.port.ResourceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

@Component
class ResourceRepositoryAdapter(
    private val resourceR2dbcRepository: ResourceR2dbcRepository
) : ResourceRepository {
    override suspend fun findById(
        id: Long
    ): Resource? = resourceR2dbcRepository.findById(
        id = id
    )?.let(ResourceMapper::toDomain)

    override suspend fun findByCode(
        code: String
    ): Resource? = resourceR2dbcRepository.findByCode(
        code = code
    )?.let(ResourceMapper::toDomain)

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

    override suspend fun findByStoreIdAndCode(
        storeId: Long,
        code: String
    ): Resource? = resourceR2dbcRepository.findByStoreIdAndCode(
        storeId = storeId,
        code = code
    )?.let(ResourceMapper::toDomain)

    override fun findAllByStoreId(
        storeId: Long
    ): Flow<Resource> = resourceR2dbcRepository.findAllByStoreId(
        storeId = storeId
    ).map(ResourceMapper::toDomain)
}