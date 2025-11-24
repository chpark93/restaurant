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

        val resource = resourceService.getByCode(code)

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = resource.toResponse()
                )
            )
    }
}