package com.chpark.restaurant.resource.domain

class Resource private constructor(
    val id: Long? = null,
    val storeId: Long,
    val code: String,
    val name: String,
    val capacity: Int,
    private var active: Boolean = true,
    val type: ResourceType
) {

    companion object {
        fun create(
            storeId: Long,
            code: String,
            name: String,
            capacity: Int,
            type: ResourceType
        ): Resource {
            require(storeId > 0) { "Resource must be associated with a store." }
            require(code.isNotBlank()) { "Resource code must not be blank." }
            require(name.isNotBlank()) { "Resource name must not be blank." }
            require(capacity > 0) { "Resource capacity must be greater than zero." }

            return Resource(
                storeId = storeId,
                code = code.trim(),
                name = name.trim(),
                capacity = capacity,
                active = true,
                type = type
            )
        }

        fun reConstruct(
            id: Long?,
            storeId: Long,
            code: String,
            name: String,
            capacity: Int,
            active: Boolean,
            type: ResourceType
        ): Resource = Resource(
            id = id,
            storeId = storeId,
            code = code,
            name = name,
            capacity = capacity,
            active = active,
            type = type
        )
    }

    fun isActive(): Boolean = active

    fun deactivate() {
        active = false
    }

    fun activate() {
        active = true
    }
}