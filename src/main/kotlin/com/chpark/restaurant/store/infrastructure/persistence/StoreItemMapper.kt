package com.chpark.restaurant.store.infrastructure.persistence

import com.chpark.restaurant.store.domain.StoreItem
import com.chpark.restaurant.store.domain.StoreItemStatus

object StoreItemMapper {

    fun toEntity(
        domain: StoreItem
    ): StoreItemEntity = StoreItemEntity(
        id = domain.id,
        storeId = domain.storeId,
        code = domain.code,
        name = domain.name,
        description = domain.description,
        price = domain.price,
        status = domain.status.name
    )

    fun toDomain(
        entity: StoreItemEntity
    ): StoreItem = StoreItem.reConstruct(
        id = entity.id,
        storeId = entity.storeId,
        code = entity.code,
        name = entity.name,
        description = entity.description,
        price = entity.price,
        status = StoreItemStatus.valueOf(entity.status)
    )
}