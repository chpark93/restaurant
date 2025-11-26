package com.chpark.restaurant.resource.presentation

import com.chpark.restaurant.common.response.ApiResponse
import com.chpark.restaurant.resource.application.ResourceService
import com.chpark.restaurant.resource.presentation.dto.ResourceDtos
import com.chpark.restaurant.resource.presentation.dto.ResourceDtos.toCommand
import com.chpark.restaurant.resource.presentation.dto.ResourceDtos.toResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.time.Instant

@Component
class ResourceHandler(
    private val resourceService: ResourceService
) {

    suspend fun createResource(
        request: ServerRequest
    ): ServerResponse {
        val body = request.awaitBody<ResourceDtos.CreateResourceRequest>()

        val createdResource = resourceService.create(
            command = body.toCommand()
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = createdResource.toResponse()
                )
            )
    }

    suspend fun getResource(
        request: ServerRequest
    ): ServerResponse {
        val code = request.pathVariable("code")

        val resource = resourceService.getByCode(
            code = code
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = resource.toResponse()
                )
            )
    }

    suspend fun getResourcesByStore(
        request: ServerRequest
    ): ServerResponse {
        val storeId = request.pathVariable("storeId").toLong()

        val result = resourceService.getByStoreId(
            storeId = storeId
        ).map { resource ->
            resource.toResponse()
        }

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = result
                )
            )
    }

    suspend fun getResourceCapacity(
        request: ServerRequest
    ): ServerResponse {
        val resourceId = request.pathVariable("id").toLong()

        val startAtParam = request.queryParam("startAt")
            .orElseThrow { IllegalArgumentException("startAt query parameter is required") }

        val endAtParam = request.queryParam("endAt")
            .orElseThrow { IllegalArgumentException("endAt query parameter is required") }

        val startAt = Instant.parse(startAtParam)
        val endAt = Instant.parse(endAtParam)

        val result = resourceService.getResourceCapacity(
            resourceId = resourceId,
            startAt = startAt,
            endAt = endAt
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = result
                )
            )
    }

    suspend fun getResourceSlots(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id")

        val startAtParam = request.queryParam("startAt")
            .orElseThrow { IllegalArgumentException("startAt query parameter is required") }

        val endAtParam = request.queryParam("endAt")
            .orElseThrow { IllegalArgumentException("endAt query parameter is required") }

        val slotMinutesParam = request.queryParam("slotMinutes")
            .orElse("30")

        val minPartySizeParam = request.queryParam("minPartySize")
            .orElse(null)

        val resourceId = id.toLong()
        val startAt = Instant.parse(startAtParam)
        val endAt = Instant.parse(endAtParam)
        val slotMinutes = slotMinutesParam.toLong()
        val minPartySize = minPartySizeParam?.toInt()

        val result = resourceService.getAvailableSlots(
            resourceId = resourceId,
            startAt = startAt,
            endAt = endAt,
            slotMinutes = slotMinutes,
            minPartySize = minPartySize
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = result
                )
            )
    }
}