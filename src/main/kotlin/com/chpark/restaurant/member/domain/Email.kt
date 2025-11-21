package com.chpark.restaurant.member.domain

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode

@JvmInline
value class Email private constructor(
    val value: String
) {
    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        fun of(
            rawEmail: String
        ): Email {
            val normalizedEmail = rawEmail.trim().lowercase()

            if (normalizedEmail.isBlank()) {
                throw BusinessException(ErrorCode.EMPTY_EMAIL)
            }

            if (!EMAIL_REGEX.matches(normalizedEmail)) {
                throw BusinessException(ErrorCode.INVALID_EMAIL_FORMAT)
            }

            return Email(
                value = normalizedEmail
            )
        }
    }

    override fun toString(): String = value
}