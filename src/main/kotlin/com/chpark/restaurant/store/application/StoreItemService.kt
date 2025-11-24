package com.chpark.restaurant.store.application

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.store.application.dto.RegisterStoreItemCommand
import com.chpark.restaurant.store.application.dto.StoreItemResult
import com.chpark.restaurant.store.domain.StoreItem
import com.chpark.restaurant.store.domain.port.StoreItemRepository
import com.chpark.restaurant.store.domain.port.StoreRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreItemService(
    private val storeRepository: StoreRepository,
    private val storeItemRepository: StoreItemRepository
) {

    @Transactional
    suspend fun registerStoreItem(
        command: RegisterStoreItemCommand
    ): Long {
        val existedStore = storeRepository.findById(
            id = command.storeId
        ) ?: throw BusinessException(ErrorCode.STORE_NOT_FOUND)

        if (!existedStore.isActive()) {
            throw BusinessException(ErrorCode.STORE_INACTIVE)
        }

        val existedStoreItemByStoreAndCode = storeItemRepository.findByStoreIdAndCode(
            storeId = command.storeId,
            code = command.code
        )

        if (existedStoreItemByStoreAndCode != null) {
            throw BusinessException(ErrorCode.STORE_ITEM_CODE_DUPLICATED)
        }

        val storeItem = StoreItem.create(
            storeId = command.storeId,
            code = command.code,
            name = command.name,
            description = command.description,
            price = command.price
        )

        val savedStoreItem = storeItemRepository.save(storeItem)

        return savedStoreItem.id!!
    }

    @Transactional(readOnly = true)
    suspend fun getStoreItemsByStore(
        storeId: Long
    ): List<StoreItemResult> = storeItemRepository.findAllByStoreIdAndStatus(
        storeId = storeId
    ).toList().map { storeItem ->
        StoreItemResult(
            id = storeItem.id!!,
            storeId = storeItem.storeId,
            code = storeItem.code,
            name = storeItem.name,
            description = storeItem.description,
            price = storeItem.price
        )
    }
}