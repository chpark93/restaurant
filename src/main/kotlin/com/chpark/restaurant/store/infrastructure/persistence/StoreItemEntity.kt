package com.chpark.restaurant.store.infrastructure.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("store_items")
data class StoreItemEntity(
    @Id
    val id: Long? = null,

    @Column("store_id")
    val storeId: Long,

    @Column("code")
    val code: String,

    @Column("name")
    val name: String,

    @Column("description")
    val description: String? = null,

    @Column("price")
    val price: BigDecimal? = null,

    @Column("status")
    val status: String
)