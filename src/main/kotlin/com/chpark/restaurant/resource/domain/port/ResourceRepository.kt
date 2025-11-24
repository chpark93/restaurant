package com.chpark.restaurant.resource.domain.port

import com.chpark.restaurant.resource.domain.Resource
import kotlinx.coroutines.flow.Flow

interface ResourceRepository {

    suspend fun findById(
        id: Long
    ): Resource?

    suspend fun findByCode(
        code: String
    ): Resource?

    suspend fun save(
        resource: Resource
    ): Resource

    suspend fun existsByCode(
        code: String
    ): Boolean

    suspend fun findByStoreIdAndCode(
        storeId: Long,
        code: String
    ): Resource?

    fun findAllByStoreId(
        storeId: Long
    ): Flow<Resource>
}