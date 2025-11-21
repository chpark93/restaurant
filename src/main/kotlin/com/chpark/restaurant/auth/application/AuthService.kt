package com.chpark.restaurant.auth.application

import com.chpark.restaurant.auth.application.dto.LoginCommand
import com.chpark.restaurant.auth.application.dto.RegisterCommand
import com.chpark.restaurant.auth.application.dto.TokenResult
import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.member.domain.Member
import com.chpark.restaurant.member.domain.MemberRole
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
    ): Long {
        if (memberRepository.existsByEmail(command.email)) {
            throw BusinessException(ErrorCode.MEMBER_EMAIL_DUPLICATED)
        }

        val encodedPassword = passwordEncoder.encode(command.password)

        val member = Member(
            email = command.email,
            password = encodedPassword,
            name = command.name,
            role = MemberRole.USER
        )

        val savedMember = memberRepository.save(
            member = member
        )

        return savedMember.id!!
    }

    @Transactional
    suspend fun login(
        command: LoginCommand
    ): TokenResult {
        val member = memberRepository.findByEmail(command.email)
            ?: throw BusinessException(ErrorCode.MEMBER_WRONG_CREDENTIAL)

        if (!passwordEncoder.matches(command.password, member.password)) {
            throw BusinessException(ErrorCode.MEMBER_WRONG_CREDENTIAL)
        }

        return tokenService.issueToken(
            subject = member.id!!.toString(),
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