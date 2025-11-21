package com.chpark.restaurant.common.exception

import com.chpark.restaurant.common.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

@Component
@Order(-2)
class GlobalExceptionHandler(
    private val objectMapper: ObjectMapper
) : WebExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handle(
        exchange: ServerWebExchange,
        exception: Throwable
    ): Mono<Void> {
        val response = exchange.response
        if (response.isCommitted) {
            return Mono.error(exception)
        }

        val (status, code, message) = when (exception) {
            is BusinessException -> {
                logger.warn("business exception: {}", exception.message)
                Triple(
                    exception.errorCode.httpStatus,
                    exception.errorCode.code,
                    exception.message
                )
            }

            is WebExchangeBindException -> {
                val firstError = exception.fieldErrors.firstOrNull()
                val message = firstError?.defaultMessage ?: ErrorCode.COMMON_INVALID.message

                logger.warn("web exchange bind exception: {}", message)
                Triple(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.COMMON_INVALID.code,
                    message
                )
            }

            is ServerWebInputException -> {
                logger.warn("server web input exception: {}", exception.message)
                Triple(
                    HttpStatus.BAD_REQUEST,
                    ErrorCode.COMMON_INVALID.code,
                    exception.reason ?: ErrorCode.COMMON_INVALID.message
                )
            }

            is ResponseStatusException -> {
                logger.warn("response status exception: {}", exception.message)
                Triple(
                    exception.statusCode,
                    ErrorCode.COMMON_INVALID.code,
                    exception.reason ?: exception.message
                )
            }

            else -> {
                logger.error("unexpected exception", exception)
                Triple(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCode.COMMON_INTERNAL_ERROR.code,
                    ErrorCode.COMMON_INTERNAL_ERROR.message
                )
            }
        }

        response.statusCode = status
        response.headers.contentType = MediaType.APPLICATION_JSON

        val body = ApiResponse.fail(
            code = code,
            message = message
        )

        val bytes = objectMapper.writeValueAsBytes(body)
        val buffer = response.bufferFactory().wrap(bytes)

        return response.writeWith(Mono.just(buffer))
    }
}