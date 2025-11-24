package com.chpark.restaurant.resource.infrastructure.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("resources")
data class ResourceEntity(
    @Id
    val id: Long? = null,

    @Column("code")
    val code: String,

    @Column("name")
    val name: String,

    @Column("capacity")
    val capacity: Int,

    @Column("active")
    val active: Boolean,

    @Column("type")
    val type: String
)