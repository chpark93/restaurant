package com.chpark.restaurant.member.domain

import org.springframework.security.crypto.password.PasswordEncoder


class Member private constructor(
    val id: Long? = null,
    val email: Email,
    private var password: String,
    val name: String,
    val role: MemberRole = MemberRole.USER
) {
    companion object {
        fun register(
            email: Email,
            password: String,
            name: String,
            role: MemberRole = MemberRole.USER,
            passwordEncoder: PasswordEncoder
        ): Member = Member(
            email = email,
            password = passwordEncoder.encode(password),
            name = name,
            role = role
        )

        fun reConstruct(
            id: Long?,
            email: Email,
            encodedPassword: String,
            name: String,
            role: MemberRole
        ): Member = Member(
            id = id,
            email = email,
            password = encodedPassword,
            name = name,
            role = role
        )
    }

    fun encodedPassword(): String = password

    fun changePassword(
        newPassword: String
    ) {
        this.password = newPassword
    }

    fun isPasswordMatch(
        rawPassword: String,
        passwordEncoder: PasswordEncoder
    ): Boolean = passwordEncoder.matches(rawPassword, password)
}