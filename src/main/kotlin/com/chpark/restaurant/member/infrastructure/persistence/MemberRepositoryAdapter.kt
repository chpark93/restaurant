package com.chpark.restaurant.member.infrastructure.persistence

import com.chpark.restaurant.member.domain.Member
import com.chpark.restaurant.member.domain.port.MemberRepository
import org.springframework.stereotype.Component

@Component
class MemberRepositoryAdapter(
    private val memberR2dbcRepository: MemberR2dbcRepository
) : MemberRepository {

    override suspend fun existsByEmail(
        email: String
    ): Boolean = memberR2dbcRepository.existsByEmail(
        email = email
    )

    override suspend fun findByEmail(
        email: String
    ): Member? = memberR2dbcRepository.findByEmail(
        email = email
    )

    override suspend fun save(
        member: Member
    ): Member = memberR2dbcRepository.save(
        entity = member
    )
}