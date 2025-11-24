package com.chpark.restaurant.store.domain

class Store private constructor(
    val id: Long? = null,
    val code: String,
    val name: String,
    val description: String?,
    val status: StoreStatus
) {

    companion object {
        fun create(
            code: String,
            name: String,
            description: String? = null
        ): Store {
            require(code.isNotBlank()) { "Store code must not be blank." }
            require(name.isNotBlank()) { "Store name must not be blank." }

            return Store(
                code = code.trim(),
                name = name.trim(),
                description = description?.trim(),
                status = StoreStatus.ACTIVE
            )
        }

        fun reConstruct(
            id: Long?,
            code: String,
            name: String,
            description: String?,
            status: StoreStatus
        ): Store = Store(
            id = id,
            code = code,
            name = name,
            description = description,
            status = status
        )
    }

    fun isActive(): Boolean = status == StoreStatus.ACTIVE
}