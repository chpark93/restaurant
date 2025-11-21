package com.chpark.restaurant.member.infrastructure.persistence

import com.chpark.restaurant.member.domain.Email
import com.chpark.restaurant.member.domain.Member
import com.chpark.restaurant.member.domain.MemberRole

object MemberMapper {

    fun toEntity(
        domain: Member
    ): MemberEntity = MemberEntity(
        id = domain.id,
        email = domain.email.value,
        password = extractPassword(
            member = domain
        ),
        name = domain.name,
        role = domain.role.name
    )

    fun toDomain(
        entity: MemberEntity
    ): Member = Member.reConstruct(
        id = entity.id,
        email = Email.of(
            rawEmail = entity.email
        ),
        encodedPassword = entity.password,
        name = entity.name,
        role = MemberRole.valueOf(
            value = entity.role
        )
    )

    private fun extractPassword(
        member: Member
    ): String = member.encodedPassword()
}