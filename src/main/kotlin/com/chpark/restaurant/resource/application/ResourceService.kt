package com.chpark.restaurant.resource.application

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.reservation.domain.TimeSlot
import com.chpark.restaurant.reservation.domain.port.ReservationRepository
import com.chpark.restaurant.resource.application.dto.CreateResourceCommand
import com.chpark.restaurant.resource.application.dto.ResourceCapacityResult
import com.chpark.restaurant.resource.domain.Resource
import com.chpark.restaurant.resource.domain.port.ResourceRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ResourceService(
    private val resourceRepository: ResourceRepository,
    private val reservationRepository: ReservationRepository
) {

    @Transactional
    suspend fun create(
        command: CreateResourceCommand
    ): Resource {
        if (resourceRepository.existsByCode(code = command.code)) {
            throw BusinessException(ErrorCode.RESOURCE_CODE_DUPLICATED)
        }

        val resource = Resource.create(
            storeId = command.storeId,
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

    @Transactional(readOnly = true)
    suspend fun getByStoreId(
        storeId: Long
    ): List<Resource> = resourceRepository.findAllByStoreId(
        storeId = storeId
    ).toList()

    @Transactional(readOnly = true)
    suspend fun getResourceCapacity(
        resourceId: Long,
        startAt: Instant,
        endAt: Instant
    ): ResourceCapacityResult {
        val resource = resourceRepository.findById(
            id = resourceId
        ) ?: throw BusinessException(ErrorCode.RESOURCE_NOT_FOUND)

        if (!resource.isActive()) {
            throw BusinessException(ErrorCode.RESOURCE_INACTIVE)
        }

        val timeSlot = TimeSlot(
            start = startAt,
            end = endAt
        )

        val reservedCount = reservationRepository.countActiveOverlapping(
            resourceId = resourceId,
            timeSlot = timeSlot
        )

        val available = (resource.capacity - reservedCount.toInt()).coerceAtLeast(0)

        return ResourceCapacityResult(
            resourceId = resource.id!!,
            totalCapacity = resource.capacity,
            reservedCount = reservedCount,
            availableCapacity = available
        )
    }
}