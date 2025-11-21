package com.chpark.restaurant.auth.application

import com.chpark.restaurant.auth.application.dto.LoginCommand
import com.chpark.restaurant.auth.application.dto.RegisterCommand
import com.chpark.restaurant.auth.application.dto.TokenResult
import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.member.domain.Email
import com.chpark.restaurant.member.domain.Member
import com.chpark.restaurant.member.domain.port.MemberRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val tokenService: TokenService
) {

    private val passwordEncoder: BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Transactional
    suspend fun register(
        command: RegisterCommand
    ): String {
        if (memberRepository.existsByEmail(Email.of(rawEmail = command.email))) {
            throw BusinessException(ErrorCode.MEMBER_EMAIL_DUPLICATED)
        }

        val member = Member.register(
            email = Email.of(
                rawEmail = command.email
            ),
            password = command.password,
            name = command.name,
            passwordEncoder = passwordEncoder
        )

        val savedMember = memberRepository.save(
            member = member
        )

        return savedMember.email.value
    }

    @Transactional
    suspend fun login(
        command: LoginCommand
    ): TokenResult {
        val member = memberRepository.findByEmail(
            email = Email.of(command.email)
        ) ?: throw BusinessException(ErrorCode.MEMBER_WRONG_CREDENTIAL)

        if (!member.isPasswordMatch(command.password, passwordEncoder)) {
            throw BusinessException(ErrorCode.MEMBER_WRONG_CREDENTIAL)
        }

        return tokenService.issueToken(
            subject = member.email.value,
            roles = listOf(member.role.name)
        )
    }

    @Transactional
    suspend fun reissue(
        refreshToken: String
    ): TokenResult = tokenService.reissueToken(
        refreshToken = refreshToken
    )

    @Transactional
    suspend fun logout(
        accessToken: String,
        refreshToken: String?
    ) {
        tokenService.logout(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}