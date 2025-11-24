package com.chpark.restaurant.store.infrastructure.persistence

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface StoreR2dbcRepository : CoroutineCrudRepository<StoreEntity, Long> {

    suspend fun findByCode(
        code: String
    ): StoreEntity?

    fun findAllByStatus(
        status: String
    ): Flow<StoreEntity>
}