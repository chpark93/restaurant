package com.chpark.restaurant.member.domain.port

import com.chpark.restaurant.member.domain.Email
import com.chpark.restaurant.member.domain.Member

interface MemberRepository {
    suspend fun existsByEmail(
        email: Email
    ): Boolean

    suspend fun findByEmail(
        email: Email
    ): Member?

    suspend fun save(
        member: Member
    ): Member
}