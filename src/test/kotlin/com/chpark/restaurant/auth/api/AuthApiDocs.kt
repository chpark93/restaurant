package com.chpark.restaurant.auth.api

import com.chpark.restaurant.auth.application.AuthService
import com.chpark.restaurant.auth.application.dto.LoginCommand
import com.chpark.restaurant.auth.application.dto.RegisterCommand
import com.chpark.restaurant.auth.application.dto.TokenResult
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
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.restdocs.payload.JsonFieldType
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

    private val commonResponseFields = arrayOf(
        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
        fieldWithPath("error").type(JsonFieldType.OBJECT).description("에러 정보 (성공 시 null)").optional(),
        fieldWithPath("error.code").type(JsonFieldType.STRING).description("에러 코드").optional(),
        fieldWithPath("error.message").type(JsonFieldType.STRING).description("에러 메시지").optional(),
        fieldWithPath("timestamp").type(JsonFieldType.STRING).description("응답 시간")
    )

    @Test
    fun register() {
        val request = RegisterCommand(
            email = "user@example.com",
            password = "1234",
            name = "홍길동"
        )

        runBlocking {
            given(authService.register(request))
                .willReturn("user@example.com")
        }

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
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("사용자 이름")
                            )
                            .responseFields(
                                *commonResponseFields,
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("등록된 이메일"),
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    fun login() {
        val request = LoginCommand(
            email = "user@example.com",
            password = "1234"
        )

        val tokenResult = TokenResult(
            accessToken = "access-token",
            refreshToken = "refresh-token"
        )

        runBlocking {
            given(authService.login(request))
                .willReturn(tokenResult)
        }

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
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("password").description("비밀번호")
                            )
                            .responseFields(
                                *commonResponseFields,
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("발급된 Access Token"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("발급된 Refresh Token")
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    fun reissue() {
        val request = AuthDtos.ReissueRequest(
            refreshToken = "refresh-token"
        )

        val tokenResult = TokenResult(
            accessToken = "new-access-token",
            refreshToken = "new-refresh-token"
        )

        runBlocking {
            given(authService.reissue(request.refreshToken))
                .willReturn(tokenResult)
        }

        webTestClient.post()
            .uri("/api/auth/reissue")
            .contentType(APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                document(
                    "auth-reissue",
                    resource(
                        ResourceSnippetParameters.builder()
                            .tag("Auth")
                            .summary("토큰 재발급")
                            .requestFields(
                                fieldWithPath("refreshToken")
                                    .description("기존 Refresh Token")
                            )
                            .responseFields(
                                *commonResponseFields,
                                fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("새로 발급된 Access Token"),
                                fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("새로 발급된 Refresh Token"),
                            )
                            .build()
                    )
                )
            )
    }

    @Test
    fun logout() {
        val request = AuthDtos.LogoutRequest(
            accessToken = "access-token",
            refreshToken = "refresh-token"
        )

        runBlocking {
            given(
                authService.logout(
                    accessToken = request.accessToken,
                    refreshToken = request.refreshToken
                )
            ).willReturn(Unit)
        }

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
                                fieldWithPath("accessToken")
                                    .description("로그아웃 처리할 Access Token"),
                                fieldWithPath("refreshToken")
                                    .description("연관된 Refresh Token (선택)")
                                    .optional()
                            )
                            .responseFields(
                                *commonResponseFields,
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("항상 null").optional()
                            )
                            .build()
                    )
                )
            )
    }
}