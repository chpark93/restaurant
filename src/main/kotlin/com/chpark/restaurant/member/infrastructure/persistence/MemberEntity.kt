package com.chpark.restaurant.member.infrastructure.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("members")
data class MemberEntity(
    @Id
    val id: Long? = null,

    @Column("email")
    val email: String,

    @Column("password")
    val password: String,

    @Column("name")
    val name: String,

    @Column("role")
    val role: String,

    @Column("created_at")
    val createdAt: LocalDateTime? = null,

    @Column("updated_at")
    val updatedAt: LocalDateTime? = null
)