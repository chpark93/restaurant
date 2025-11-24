package com.chpark.restaurant.store.application.dto

import java.math.BigDecimal

data class StoreItemResult(
    val id: Long,
    val storeId: Long,
    val code: String,
    val name: String,
    val description: String?,
    val price: BigDecimal?
)