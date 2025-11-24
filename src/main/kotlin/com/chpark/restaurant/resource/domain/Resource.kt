package com.chpark.restaurant.resource.domain

class Resource private constructor(
    val id: Long? = null,
    val code: String,
    val name: String,
    val capacity: Int,
    private var active: Boolean = true,
    val type: ResourceType
) {

    companion object {
        fun create(
            code: String,
            name: String,
            capacity: Int,
            type: ResourceType
        ): Resource {
            require(code.isNotBlank()) { "Resource code must not be blank." }
            require(name.isNotBlank()) { "Resource name must not be blank." }
            require(capacity > 0) { "Resource capacity must be greater than zero." }

            return Resource(
                code = code.trim(),
                name = name.trim(),
                capacity = capacity,
                active = true,
                type = type
            )
        }

        fun reConstruct(
            id: Long?,
            code: String,
            name: String,
            capacity: Int,
            active: Boolean,
            type: ResourceType
        ): Resource = Resource(
            id = id,
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