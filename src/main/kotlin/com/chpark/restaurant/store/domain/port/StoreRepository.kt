package com.chpark.restaurant.store.domain.port

import com.chpark.restaurant.store.domain.Store
import com.chpark.restaurant.store.domain.StoreStatus
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    suspend fun save(
        store: Store
    ): Store

    suspend fun findById(
        id: Long
    ): Store?

    suspend fun findByCode(
        code: String
    ): Store?

    fun findAllByStatus(
        status: StoreStatus = StoreStatus.ACTIVE
    ): Flow<Store>
}