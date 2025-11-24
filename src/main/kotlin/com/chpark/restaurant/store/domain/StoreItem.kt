package com.chpark.restaurant.store.domain

import java.math.BigDecimal

class StoreItem private constructor(
    val id: Long? = null,
    val storeId: Long,
    val code: String,
    val name: String,
    val description: String?,
    val price: BigDecimal?,
    val status: StoreItemStatus
) {

    companion object {
        fun create(
            storeId: Long,
            code: String,
            name: String,
            description: String? = null,
            price: BigDecimal? = null
        ): StoreItem {
            require(storeId > 0) { "StoreItem must be associated with a store." }
            require(code.isNotBlank()) { "StoreItem code must not be blank." }
            require(name.isNotBlank()) { "StoreItem name must not be blank." }

            return StoreItem(
                storeId = storeId,
                code = code.trim(),
                name = name.trim(),
                description = description?.trim(),
                price = price,
                status = StoreItemStatus.ACTIVE
            )
        }

        fun reConstruct(
            id: Long?,
            storeId: Long,
            code: String,
            name: String,
            description: String?,
            price: BigDecimal?,
            status: StoreItemStatus
        ): StoreItem = StoreItem(
            id = id,
            storeId = storeId,
            code = code,
            name = name,
            description = description,
            price = price,
            status = status
        )
    }

    fun isActive(): Boolean = status == StoreItemStatus.ACTIVE
}