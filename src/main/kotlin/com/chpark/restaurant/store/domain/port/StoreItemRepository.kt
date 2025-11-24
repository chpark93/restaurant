package com.chpark.restaurant.store.domain.port

import com.chpark.restaurant.store.domain.StoreItem
import com.chpark.restaurant.store.domain.StoreItemStatus
import kotlinx.coroutines.flow.Flow

interface StoreItemRepository {
    suspend fun save(
        item: StoreItem
    ): StoreItem

    suspend fun findById(
        id: Long
    ): StoreItem?

    suspend fun findByStoreIdAndCode(
        storeId: Long,
        code: String
    ): StoreItem?

    fun findAllByStoreIdAndStatus(
        storeId: Long,
        status: StoreItemStatus = StoreItemStatus.ACTIVE
    ): Flow<StoreItem>
}