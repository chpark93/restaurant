package com.chpark.restaurant.member.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("members")
data class Member(
    @Id
    val id: Long? = null,

    @Column("email")
    val email: String,

    @Column("password")
    val password: String,

    @Column("name")
    val name: String,

    @Column("role")
    val role: MemberRole = MemberRole.USER
)