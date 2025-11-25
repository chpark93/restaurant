package com.chpark.restaurant.auth.api

import com.chpark.restaurant.auth.application.AuthService
import com.chpark.restaurant.auth.application.dto.LoginCommand
import com.chpark.restaurant.auth.application.dto.RegisterCommand
import com.chpark.restaurant.auth.application.dto.TokenResult
import com.chpark.restaurant.auth.infrastructure.jwt.JwtAuthenticationWebFilter
import com.chpark.restaurant.auth.infrastructure.jwt.JwtProperties
import com.chpark.restaurant.auth.infrastructure.jwt.JwtTokenParser
import com.chpark.restaurant.auth.infrastructure.jwt.JwtTokenProvider
import com.chpark.restaurant.auth.presentation.AuthHandler
import com.chpark.restaurant.auth.presentation.AuthRouter
import com.chpark.restaurant.auth.presentation.dto.AuthDtos
import com.chpark.restaurant.support.RestDocsSupport
import com.chpark.restaurant.support.TestSecurityConfig
import com.epages.restdocs.apispec.ResourceDocumentation.resource
import com.epages.restdocs.apispec.ResourceSnippetParameters
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document
import org.springframework.test.context.bean.override.mockito.MockitoBean

@Import(
    AuthRouter::class,
    AuthHandler::class,
    TestSecurityConfig::class
)
class AuthApiDocs : RestDocsSupport() {

    @MockitoBean
    private lateinit var authService: AuthService
    @MockitoBean
    private lateinit var jwtTokenProvider: JwtTokenProvider
    @MockitoBean
    private lateinit var jwtProperties: JwtProperties
    @MockitoBean
    private lateinit var jwtTokenParser: JwtTokenParser
    @MockitoBean
    private lateinit var jwtAuthenticationWebFilter: JwtAuthenticationWebFilter

    @Test
    fun register() {
        runBlocking {
            // given
            val request = RegisterCommand(
                email = "user@example.com",
                password = "1234",
                name = "홍길동"
            )

            given(authService.register(request))
                .willReturn(request.email)

            // when & then
            webTestClient.post()
                .uri("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(
                    document(
                        "auth-register",
                        resource(
                            ResourceSnippetParameters.builder()
                                .tag("Auth")
                                .summary("회원가입")
                                .requestFields(
                                    fieldWithPath("email").type(STRING).description("회원 이메일"),
                                    fieldWithPath("password").type(STRING).description("비밀번호"),
                                    fieldWithPath("name").type(STRING).description("사용자 이름")
                                )
                                .responseFields(
                                    fieldWithPath("success").type(BOOLEAN).description("성공 여부"),
                                    fieldWithPath("data.email").type(STRING).description("등록된 이메일"),
                                    fieldWithPath("error").type(OBJECT).optional()
                                        .description("에러 정보 (성공 시 null)"),
                                    fieldWithPath("error.code").type(STRING).optional()
                                        .description("에러 코드"),
                                    fieldWithPath("error.message").type(STRING).optional()
                                        .description("에러 메시지"),
                                    fieldWithPath("timestamp").type(STRING).description("응답 시간")
                                )
                                .build()
                        )
                    )
                )
        }
    }

    @Test
    fun login() {
        runBlocking {
            // given
            val request = LoginCommand(
                email = "user@example.com",
                password = "1234"
            )

            val tokenResult = TokenResult(
                accessToken = "access-token",
                refreshToken = "refresh-token"
            )

            given(authService.login(request))
                .willReturn(tokenResult)

            // when & then
            webTestClient.post()
                .uri("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(
                    document(
                        "auth-login",
                        resource(
                            ResourceSnippetParameters.builder()
                                .tag("Auth")
                                .summary("로그인")
                                .requestFields(
                                    fieldWithPath("email").type(STRING).description("회원 이메일"),
                                    fieldWithPath("password").type(STRING).description("비밀번호")
                                )
                                .responseFields(
                                    fieldWithPath("success").type(BOOLEAN).description("성공 여부"),
                                    fieldWithPath("data.accessToken").type(STRING)
                                        .description("발급된 Access Token"),
                                    fieldWithPath("data.refreshToken").type(STRING)
                                        .description("발급된 Refresh Token"),
                                    fieldWithPath("error").type(OBJECT).optional()
                                        .description("에러 정보 (성공 시 null)"),
                                    fieldWithPath("error.code").type(STRING).optional()
                                        .description("에러 코드"),
                                    fieldWithPath("error.message").type(STRING).optional()
                                        .description("에러 메시지"),
                                    fieldWithPath("timestamp").type(STRING).description("응답 시간")
                                )
                                .build()
                        )
                    )
                )
        }
    }

    @Test
    fun reissue() {
        runBlocking {
            // given
            val refreshToken = "dummy-refresh-token"

            val request = AuthDtos.ReissueRequest(
                refreshToken = refreshToken
            )

            val tokenResult = TokenResult(
                accessToken = "new-access-token",
                refreshToken = "new-refresh-token"
            )

            given(authService.reissue(refreshToken))
                .willReturn(tokenResult)

            // when & then
            webTestClient.post()
                .uri("/api/auth/reissue")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(
                    document(
                        "auth-reissue-token",
                        resource(
                            ResourceSnippetParameters.builder()
                                .tag("Auth")
                                .summary("토큰 재발급")
                                .requestFields(
                                    fieldWithPath("refreshToken").type(STRING)
                                        .description("기존 Refresh Token")
                                )
                                .responseFields(
                                    fieldWithPath("success").type(BOOLEAN)
                                        .description("요청 성공 여부"),
                                    fieldWithPath("data.accessToken").type(STRING)
                                        .description("새로 발급된 Access Token"),
                                    fieldWithPath("data.refreshToken").type(STRING)
                                        .description("새로 발급된 Refresh Token"),
                                    fieldWithPath("error").type(OBJECT).optional()
                                        .description("에러 정보 (실패 시)"),
                                    fieldWithPath("error.code").type(STRING).optional()
                                        .description("에러 코드"),
                                    fieldWithPath("error.message").type(STRING).optional()
                                        .description("에러 메시지"),
                                    fieldWithPath("timestamp").type(STRING)
                                        .description("응답 생성 시각")
                                )
                                .build()
                        )
                    )
                )
        }
    }

    @Test
    fun logout() {
        runBlocking {
            // given
            val request = AuthDtos.LogoutRequest(
                accessToken = "dummy-access-token",
                refreshToken = "dummy-refresh-token"
            )

            willDoNothing().given(authService)
                .logout(accessToken = request.accessToken, refreshToken = request.refreshToken)

            // when & then
            webTestClient.post()
                .uri("/api/auth/logout")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith(
                    document(
                        "auth-logout",
                        resource(
                            ResourceSnippetParameters.builder()
                                .tag("Auth")
                                .summary("로그아웃")
                                .requestFields(
                                    fieldWithPath("accessToken").type(STRING)
                                        .description("로그아웃 처리할 Access Token"),
                                    fieldWithPath("refreshToken").type(STRING)
                                        .description("연관된 Refresh Token (선택)").optional()
                                )
                                .responseFields(
                                    fieldWithPath("success").type(BOOLEAN)
                                        .description("요청 성공 여부"),
                                    fieldWithPath("data").type(NULL)
                                        .description("항상 null").optional(),
                                    fieldWithPath("error").type(OBJECT).optional()
                                        .description("에러 정보 (실패 시)"),
                                    fieldWithPath("error.code").type(STRING).optional()
                                        .description("에러 코드"),
                                    fieldWithPath("error.message").type(STRING).optional()
                                        .description("에러 메시지"),
                                    fieldWithPath("timestamp").type(STRING)
                                        .description("응답 생성 시각")
                                )
                                .build()
                        )
                    )
                )
        }
    }
}