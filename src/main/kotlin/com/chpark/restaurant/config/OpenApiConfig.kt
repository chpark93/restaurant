package com.chpark.restaurant.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse
import io.swagger.v3.oas.models.responses.ApiResponses
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class OpenApiConfig {

    @Bean
    fun reservationOpenAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Restaurant API")
                    .description("실시간 좌석/타임 슬롯 예약 + 웨이팅 시스템 API")
                    .version("v1")
            )
            .components(Components())

    @Bean
    fun globalResponsesCustomizer(): OpenApiCustomizer =
        OpenApiCustomizer { openApi ->
            val paths = openApi.paths ?: return@OpenApiCustomizer

            paths.values.forEach { pathItem ->
                pathItem.readOperations().forEach { operation ->
                    val responses: ApiResponses = operation.responses

                    addErrorResponse(responses, "400", "Bad Reqeust.")
                    addErrorResponse(responses, "401", "인증이 필요합니다.")
                    addErrorResponse(responses, "403", "접근 권한이 없습니다.")
                    addErrorResponse(responses, "500", "서버 오류가 발생했습니다.")
                }
            }
        }

    private fun addErrorResponse(
        responses: ApiResponses,
        statusCode: String,
        description: String
    ) {
        if (responses.containsKey(statusCode)) return

        val apiResponseSchema = Schema<Map<String, Any>>()
            .addProperty("success", Schema<Boolean>().example(false))
            .addProperty("data", Schema<Any>().nullable(true))
            .addProperty(
                "error",
                Schema<Map<String, Any>>()
                    .addProperty("code", Schema<String>())
                    .addProperty("message", Schema<String>())
            )
            .addProperty("timestamp", Schema<String>().example(LocalDateTime.now()))

        val mediaType = MediaType().schema(apiResponseSchema)

        val apiResponse = ApiResponse()
            .description(description)
            .content(
                Content().addMediaType("application/json", mediaType)
            )

        responses.addApiResponse(statusCode, apiResponse)
    }
}