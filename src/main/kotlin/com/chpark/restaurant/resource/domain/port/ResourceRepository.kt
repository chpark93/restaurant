package com.chpark.restaurant.resource.domain.port

import com.chpark.restaurant.resource.domain.Resource

interface ResourceRepository {

    suspend fun findByCode(
        code: String
    ): Resource?

    suspend fun save(
        resource: Resource
    ): Resource

    suspend fun existsByCode(
        code: String
    ): Boolean
}