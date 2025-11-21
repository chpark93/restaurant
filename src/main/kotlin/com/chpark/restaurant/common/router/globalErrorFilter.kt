package com.chpark.restaurant.common.router

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.common.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.HandlerFilterFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

fun globalErrorFilter(): HandlerFilterFunction<ServerResponse, ServerResponse> =

    HandlerFilterFunction { request: ServerRequest, next ->
        next.handle(request)
            .onErrorResume(BusinessException::class.java) { exception ->
                ServerResponse
                    .status(exception.errorCode.httpStatus)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                        ApiResponse.fail(
                            code = exception.errorCode.code,
                            message = exception.message
                        )
                    )
            }.onErrorResume(Throwable::class.java) { _ ->
                ServerResponse
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(
                        ApiResponse.fail(
                            code = ErrorCode.COMMON_INTERNAL_ERROR.code,
                            message = ErrorCode.COMMON_INTERNAL_ERROR.message
                        )
                    )
            }
    }