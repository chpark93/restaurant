package com.chpark.restaurant.resource.infrastructure.persistence

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ResourceR2dbcRepository : CoroutineCrudRepository<ResourceEntity, Long> {

    suspend fun findByCode(
        code: String
    ): ResourceEntity?

    suspend fun existsByCode(
        code: String
    ): Boolean

    suspend fun findByStoreIdAndCode(
        storeId: Long,
        code: String
    ): ResourceEntity?

    fun findAllByStoreId(
        storeId: Long
    ): Flow<ResourceEntity>
}