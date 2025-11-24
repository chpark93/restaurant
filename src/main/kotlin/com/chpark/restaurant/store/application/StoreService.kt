package com.chpark.restaurant.store.application

import com.chpark.restaurant.common.exception.BusinessException
import com.chpark.restaurant.common.exception.ErrorCode
import com.chpark.restaurant.store.application.dto.RegisterStoreCommand
import com.chpark.restaurant.store.application.dto.StoreResult
import com.chpark.restaurant.store.domain.Store
import com.chpark.restaurant.store.domain.port.StoreRepository
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StoreService(
    private val storeRepository: StoreRepository
) {

    @Transactional
    suspend fun register(
        command: RegisterStoreCommand
    ): Long {
        val existedStoreByCode = storeRepository.findByCode(
            code = command.code
        )

        if (existedStoreByCode != null) {
            throw BusinessException(ErrorCode.STORE_CODE_DUPLICATED)
        }

        val store = Store.create(
            code = command.code,
            name = command.name,
            description = command.description
        )

        val savedStore = storeRepository.save(store)

        return savedStore.id!!
    }

    @Transactional(readOnly = true)
    suspend fun getStore(
        id: Long
    ): StoreResult {
        val store = storeRepository.findById(
            id = id
        ) ?: throw BusinessException(ErrorCode.STORE_NOT_FOUND)

        return StoreResult(
            id = store.id!!,
            code = store.code,
            name = store.name,
            description = store.description
        )
    }

    @Transactional(readOnly = true)
    suspend fun getActiveStores(): List<StoreResult> =
        storeRepository.findAllByStatus().toList().map {
            StoreResult(
                id = it.id!!,
                code = it.code,
                name = it.name,
                description = it.description
            )
        }
}