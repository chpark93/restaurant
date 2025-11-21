package com.chpark.restaurant.member.infrastructure.persistence

import com.chpark.restaurant.member.domain.Email
import com.chpark.restaurant.member.domain.Member
import com.chpark.restaurant.member.domain.port.MemberRepository
import org.springframework.stereotype.Component

@Component
class MemberRepositoryAdapter(
    private val memberR2dbcRepository: MemberR2dbcRepository
) : MemberRepository {

    override suspend fun existsByEmail(
        email: Email
    ): Boolean = memberR2dbcRepository.existsByEmail(
        email = email.value
    )

    override suspend fun findByEmail(
        email: Email
    ): Member? = memberR2dbcRepository.findByEmail(
        email = email.value
    )?.let { entity ->
        MemberMapper.toDomain(
            entity = entity
        )
    }

    override suspend fun save(
        member: Member
    ): Member {
        val savedMember = memberR2dbcRepository.save(
            entity = MemberMapper.toEntity(
                domain = member
            )
        )

        return MemberMapper.toDomain(
            entity = savedMember
        )
    }
}