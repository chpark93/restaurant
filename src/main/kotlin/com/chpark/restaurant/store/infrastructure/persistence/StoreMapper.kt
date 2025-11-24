package com.chpark.restaurant.store.infrastructure.persistence

import com.chpark.restaurant.store.domain.Store
import com.chpark.restaurant.store.domain.StoreStatus

object StoreMapper {

    fun toEntity(
        domain: Store
    ): StoreEntity = StoreEntity(
        id = domain.id,
        code = domain.code,
        name = domain.name,
        description = domain.description,
        status = domain.status.name
    )

    fun toDomain(
        entity: StoreEntity
    ): Store = Store.reConstruct(
        id = entity.id,
        code = entity.code,
        name = entity.name,
        description = entity.description,
        status = StoreStatus.valueOf(entity.status)
    )
}