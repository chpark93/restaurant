package com.chpark.restaurant.store.infrastructure.persistence

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreItemR2dbcRepository : CoroutineCrudRepository<StoreItemEntity, Long> {

    suspend fun findByStoreIdAndCode(
        storeId: Long,
        code: String
    ): StoreItemEntity?

    fun findAllByStoreIdAndStatus(
        storeId: Long,
        status: String
    ): Flow<StoreItemEntity>
}