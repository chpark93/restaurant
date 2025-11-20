package com.chpark.restaurant.common.exception

import com.chpark.restaurant.common.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(
        exception: BusinessException
    ): ResponseEntity<ApiResponse<Unit>> {
        val code = exception.errorCode

        return ResponseEntity
            .status(code.httpStatus)
            .body(
                ApiResponse.fail(
                    code.code,
                    exception.message
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(): ResponseEntity<ApiResponse<Unit>> {
        val code = ErrorCode.COMMON_INTERNAL_ERROR

        return ResponseEntity
            .status(code.httpStatus)
            .body(
                ApiResponse.fail(
                    code.code,
                    code.message
                )
            )
    }
}