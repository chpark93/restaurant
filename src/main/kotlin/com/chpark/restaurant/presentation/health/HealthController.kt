package com.chpark.restaurant.presentation.health

import com.chpark.restaurant.common.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/api/health")
    suspend fun health(): ApiResponse<String> {
        return ApiResponse.ok("OK")
    }
}