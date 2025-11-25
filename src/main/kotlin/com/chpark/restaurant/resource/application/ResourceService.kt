package com.chpark.restaurant.resource.application

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.reservation.domain.TimeSlot
import com.chpark.restaurant.reservation.domain.port.ReservationRepository
import com.chpark.restaurant.resource.application.dto.CreateResourceCommand
import com.chpark.restaurant.resource.application.dto.ResourceCapacityResult
import com.chpark.restaurant.resource.application.dto.ResourceSlotAvailability
import com.chpark.restaurant.resource.domain.Resource
import com.chpark.restaurant.resource.domain.port.ResourceRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
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

    @Transactional(readOnly = true)
    suspend fun getAvailableSlots(
        resourceId: Long,
        startAt: Instant,
        endAt: Instant,
        slotMinutes: Long,
        minPartySize: Int?
    ): List<ResourceSlotAvailability> {
        if (!startAt.isBefore(endAt)) {
            throw BusinessException(ErrorCode.RESOURCE_INVALID_TIME_SLOT)
        }

        val resource = resourceRepository.findById(
            id = resourceId
        ) ?: throw BusinessException(ErrorCode.RESOURCE_NOT_FOUND)

        val slotStep = Duration.ofMinutes(slotMinutes)

        val baseSlot = TimeSlot(
            start = startAt,
            end = endAt
        )
        val reservations = reservationRepository.findOverlapping(
            resourceId = resource.id!!,
            timeSlot = baseSlot
        )

        val timeSlots = mutableListOf<TimeSlot>()
        var cursor = startAt
        while (cursor.isBefore(endAt)) {
            val next = cursor.plus(slotStep)
            if (!next.isAfter(endAt)) {
                timeSlots.add(TimeSlot(
                    start = cursor,
                    end = next
                ))
            } else {
                break
            }

            cursor = next
        }

        return timeSlots.map { slot ->
            val reservedCount = reservations.filter {
                it.timeSlot.overlaps(
                    timeSlot = slot
                )
            }.sumOf { it.partySize }

            val available = (resource.capacity - reservedCount).coerceAtLeast(0)
            val canReserve = if (minPartySize != null) {
                available >= minPartySize
            } else {
                available > 0
            }

            ResourceSlotAvailability(
                resourceId = resource.id,
                startAt = slot.start,
                endAt = slot.end,
                capacity = resource.capacity,
                reserved = reservedCount,
                available = available,
                canReserve = canReserve
            )
        }
    }
}