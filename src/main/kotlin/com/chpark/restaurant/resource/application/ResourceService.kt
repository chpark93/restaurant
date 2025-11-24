package com.chpark.restaurant.resource.application

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.resource.application.dto.CreateResourceCommand
import com.chpark.restaurant.resource.domain.Resource
import com.chpark.restaurant.resource.domain.port.ResourceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ResourceService(
    private val resourceRepository: ResourceRepository
) {

    @Transactional
    suspend fun create(
        command: CreateResourceCommand
    ): Resource {
        if (resourceRepository.existsByCode(code = command.code)) {
            throw BusinessException(ErrorCode.RESOURCE_CODE_DUPLICATED)
        }

        val resource = Resource.create(
            code = command.code,
            name = command.name,
            capacity = command.capacity,
            type = command.type
        )

        return resourceRepository.save(resource)
    }

    @Transactional(readOnly = true)
    suspend fun getByCode(
        code: String
    ): Resource = resourceRepository.findByCode(
        code = code
    ) ?: throw BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
}