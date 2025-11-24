package com.chpark.restaurant.store.infrastructure.persistence

import com.chpark.restaurant.store.domain.StoreItem
import com.chpark.restaurant.store.domain.StoreItemStatus
import com.chpark.restaurant.store.domain.port.StoreItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

@Component
class StoreItemRepositoryAdapter(
    private val storeItemR2dbcRepository: StoreItemR2dbcRepository
) : StoreItemRepository {

    override suspend fun save(
        item: StoreItem
    ): StoreItem = StoreItemMapper.toDomain(
        storeItemR2dbcRepository.save(
            StoreItemMapper.toEntity(
                domain = item
            )
        )
    )

    override suspend fun findById(
        id: Long
    ): StoreItem? = storeItemR2dbcRepository.findById(
        id = id
    )?.let(StoreItemMapper::toDomain)

    override suspend fun findByStoreIdAndCode(
        storeId: Long,
        code: String
    ): StoreItem? = storeItemR2dbcRepository.findByStoreIdAndCode(
        storeId = storeId,
        code = code
    )?.let(StoreItemMapper::toDomain)

    override fun findAllByStoreIdAndStatus(
        storeId: Long,
        status: StoreItemStatus
    ): Flow<StoreItem> = storeItemR2dbcRepository.findAllByStoreIdAndStatus(
        storeId = storeId,
        status = status.name
    ).map(StoreItemMapper::toDomain)
}