package com.chpark.restaurant.store.infrastructure.persistence

import com.chpark.restaurant.store.domain.Store
import com.chpark.restaurant.store.domain.StoreStatus
import com.chpark.restaurant.store.domain.port.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component

@Component
class StoreRepositoryAdapter(
    private val storeR2dbcRepository: StoreR2dbcRepository
) : StoreRepository {

    override suspend fun save(
        store: Store
    ): Store = StoreMapper.toDomain(
        storeR2dbcRepository.save(
            StoreMapper.toEntity(
                domain = store
            )
        )
    )

    override suspend fun findById(
        id: Long
    ): Store? = storeR2dbcRepository.findById(
        id = id
    )?.let(StoreMapper::toDomain)

    override suspend fun findByCode(
        code: String
    ): Store? = storeR2dbcRepository.findByCode(
        code = code
    )?.let(StoreMapper::toDomain)

    override fun findAllByStatus(
        status: StoreStatus
    ): Flow<Store> = storeR2dbcRepository.findAllByStatus(
        status = status.name
    ).map(StoreMapper::toDomain)
}