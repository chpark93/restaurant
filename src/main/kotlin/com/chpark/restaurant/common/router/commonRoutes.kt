package com.chpark.restaurant.common.router

import org.springframework.web.reactive.function.server.CoRouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

fun commonRoutes(
    builder: CoRouterFunctionDsl.() -> Unit
): RouterFunction<ServerResponse> = coRouter {
    builder()
}.filter(globalErrorFilter())