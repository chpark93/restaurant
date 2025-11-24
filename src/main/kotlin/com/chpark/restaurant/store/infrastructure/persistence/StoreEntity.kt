package com.chpark.restaurant.store.infrastructure.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("stores")
data class StoreEntity(
    @Id
    val id: Long? = null,

    @Column("code")
    val code: String,

    @Column("name")
    val name: String,

    @Column("description")
    val description: String? = null,

    @Column("status")
    val status: String
)