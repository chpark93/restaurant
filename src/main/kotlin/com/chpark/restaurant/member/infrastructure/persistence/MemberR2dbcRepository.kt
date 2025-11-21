package com.chpark.restaurant.member.infrastructure.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MemberR2dbcRepository : CoroutineCrudRepository<MemberEntity, Long> {
    suspend fun existsByEmail(
        email: String
    ): Boolean

    suspend fun findByEmail(
        email: String
    ): MemberEntity?
}