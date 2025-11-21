package com.chpark.restaurant.member.domain.port

import com.chpark.restaurant.member.domain.Member

interface MemberRepository {
    suspend fun existsByEmail(
        email: String
    ): Boolean

    suspend fun findByEmail(
        email: String
    ): Member?

    suspend fun save(
        member: Member
    ): Member
}