package com.chpark.restaurant.store.presentation

import com.chpark.restaurant.common.response.ApiResponse
import com.chpark.restaurant.store.application.StoreItemService
import com.chpark.restaurant.store.application.StoreService
import com.chpark.restaurant.store.application.dto.RegisterStoreCommand
import com.chpark.restaurant.store.application.dto.RegisterStoreItemCommand
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class StoreHandler(
    private val storeService: StoreService,
    private val storeItemService: StoreItemService
) {

    suspend fun registerStore(
        request: ServerRequest
    ): ServerResponse {
        val command = request.bodyToMono(RegisterStoreCommand::class.java)
            .awaitSingle()

        val storeId = storeService.register(
            command = command
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = mapOf("id" to storeId)
                )
            )
    }

    suspend fun getStore(
        request: ServerRequest
    ): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val result = storeService.getStore(
            id = id
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = result
                )
            )
    }

    suspend fun getStores(
        request: ServerRequest
    ): ServerResponse {
        val results = storeService.getActiveStores()

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = results
                )
            )
    }

    suspend fun registerItem(
        request: ServerRequest
    ): ServerResponse {
        val command = request.bodyToMono(RegisterStoreItemCommand::class.java)
            .awaitSingle()

        val storeItemId = storeItemService.registerStoreItem(
            command = command
        )

        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                body = ApiResponse.ok(
                    data = mapOf("id" to storeItemId)
                )
            )
    }

    suspend fun getItemsByStore(
        request: ServerRequest
    ): ServerResponse {
        val storeId = request.pathVariable("storeId").toLong()

        val result = storeItemService.getStoreItemsByStore(
            storeId = storeId
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