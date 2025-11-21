package com.chpark.restaurant.auth.presentation

import com.chpark.restaurant.auth.application.AuthService
import com.chpark.restaurant.auth.application.dto.LoginCommand
import com.chpark.restaurant.auth.application.dto.RegisterCommand
import com.chpark.restaurant.auth.application.dto.TokenResult
import com.chpark.restaurant.common.response.ApiResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class AuthHandler(
    private val authService: AuthService
) {

    suspend fun register(
        request: ServerRequest
    ): ServerResponse {
        val command: RegisterCommand = request.awaitBody()

        val result = authService.register(
            command = command
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                ApiResponse.ok(
                    data = mapOf("id" to result)
                )
            )
    }

    suspend fun login(
        request: ServerRequest
    ): ServerResponse {
        val command: LoginCommand = request.awaitBody()

        val result: TokenResult = authService.login(command)

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                ApiResponse.ok(
                    data = result
                )
            )
    }

    suspend fun reissue(
        request: ServerRequest
    ): ServerResponse {
        val body = request.awaitBody<Map<String, String>>()
        val refreshToken = body["refreshToken"]
            ?: return ServerResponse.badRequest()
                .bodyValueAndAwait(ApiResponse.fail("C001", "refreshToken is required"))

        val result = authService.reissue(
            refreshToken = refreshToken
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                ApiResponse.ok(
                    data = result
                )
            )
    }

    suspend fun logout(
        request: ServerRequest
    ): ServerResponse {
        val body = request.awaitBody<Map<String, String>>()
        val accessToken = body["accessToken"]
        val refreshToken = body["refreshToken"]

        if (accessToken.isNullOrBlank()) {
            return ServerResponse.badRequest()
                .bodyValueAndAwait(
                    body = ApiResponse.fail(
                        code = "C001", "accessToken is required")
                )
        }

        authService.logout(
            accessToken = accessToken,
            refreshToken = refreshToken
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok()
            )
    }
}