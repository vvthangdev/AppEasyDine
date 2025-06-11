package com.module.domain.api.repository

import com.module.core.network.model.BaseResponse
import com.module.core.network.model.Result
import com.module.domain.api.interfaces.OrderApiInterface
import com.module.domain.api.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface OrderRepository {
    suspend fun getAllOrders(): Flow<Result<List<OrderResponse>>>
    suspend fun createOrder(request: CreateOrderRequest): Flow<Result<Unit>>
    suspend fun getOrderInfo(orderId: String?, tableId: String?): Flow<Result<OrderInfoResponse>>
    suspend fun confirmOrder(request: ConfirmOrderRequest): Flow<Result<Unit>>
    suspend fun splitOrder(request: SplitOrderRequest): Flow<Result<Unit>>
    suspend fun mergeOrder(request: MergeOrderRequest): Flow<Result<Unit>>
    suspend fun updateOrder(request: UpdateOrderRequest): Flow<Result<Unit>>
    suspend fun addItemsToOrder(request: AddItemsToOrderRequest): Flow<Result<Unit>>
    suspend fun reserveTable(request: ReserveTableRequest): Flow<Result<Unit>>
}

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderApiInterface: OrderApiInterface
) : OrderRepository {

    private suspend fun <T> safeApiCall(
        logMessage: String,
        apiCall: suspend () -> BaseResponse<T>
    ): Flow<Result<T>> = flow {
        Timber.d(logMessage)
        try {
            emit(Result.Loading)
            val response = apiCall()
            Timber.d("Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Timber.e("API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Timber.e(e, "HTTP error: ${e.code()} - ${e.message}")
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error: ${e.message}")
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllOrders() = safeApiCall("Fetching all orders") {
        orderApiInterface.getAllOrders()
    }

    // Trong file com.module.domain.api.repository.OrderRepositoryImpl
    override suspend fun reserveTable(request: ReserveTableRequest) = safeApiCall("Reserving table: $request") {
        orderApiInterface.reserveTable(request)
    }.mapSuccess { Unit } // Chuyển đổi Any? thành Unit

    override suspend fun createOrder(request: CreateOrderRequest) = safeApiCall("Creating order: $request") {
        orderApiInterface.createOrder(request)
    }.mapSuccess { Unit } // Chuyển đổi Any? thành Unit

    override suspend fun getOrderInfo(orderId: String?, tableId: String?) = flow {
        Timber.d("Fetching order info: orderId=$orderId, tableId=$tableId")
        try {
            when {
                orderId == null && tableId == null -> {
                    Timber.e("Cả orderId và tableId đều là null")
                    emit(Result.Error(Exception("Cần cung cấp orderId hoặc tableId"), "Cần cung cấp orderId hoặc tableId"))
                    return@flow
                }
                orderId != null && tableId != null -> {
                    Timber.e("Cả orderId và tableId đều được cung cấp")
                    emit(Result.Error(Exception("Chỉ được cung cấp một trong hai: orderId hoặc tableId"), "Chỉ được cung cấp một trong hai: orderId hoặc tableId"))
                    return@flow
                }
                else -> {
                    emit(Result.Loading)
                    val response = orderApiInterface.getOrderInfo(orderId, tableId)
                    Timber.d("Received response: $response")
                    if (response.isSuccess()) {
                        emit(Result.Success(response.data))
                    } else {
                        Timber.e("API error: ${response.message}")
                        emit(Result.Error(Exception("API error: ${response.message}"), response.message))
                    }
                }
            }
        } catch (e: HttpException) {
            Timber.e(e, "HTTP error: ${e.code()} - ${e.message}")
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error: ${e.message}")
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun confirmOrder(request: ConfirmOrderRequest) = safeApiCall("Confirming order: ${request.orderId}") {
        orderApiInterface.confirmOrder(request)
    }.mapSuccess { Unit } // Chuyển đổi Any? thành Unit

    override suspend fun splitOrder(request: SplitOrderRequest) = safeApiCall("Splitting order: $request") {
        orderApiInterface.splitOrder(request)
    }.mapSuccess { Unit } // Chuyển đổi Any? thành Unit

    override suspend fun mergeOrder(request: MergeOrderRequest) = safeApiCall("Merging order: $request") {
        orderApiInterface.mergeOrder(request)
    }.mapSuccess { Unit } // Chuyển đổi Any? thành Unit

    override suspend fun updateOrder(request: UpdateOrderRequest) = safeApiCall("Updating order: $request") {
        orderApiInterface.updateOrder(request)
    }.mapSuccess { Unit } // Chuyển đổi Any? thành Unit

    override suspend fun addItemsToOrder(request: AddItemsToOrderRequest) = safeApiCall("Adding items to order: $request") {
        orderApiInterface.addItemsToOrder(request)
    }.mapSuccess { Unit } // Chuyển đổi Any? thành Unit

    // Hàm hỗ trợ để chuyển đổi Result.Success<T> thành Result.Success<Unit>
    private fun <T> Flow<Result<T>>.mapSuccess(transform: (T) -> Unit): Flow<Result<Unit>> = flow {
        collect { result ->
            when (result) {
                is Result.Loading -> emit(Result.Loading)
                is Result.Success -> {
                    result.data?.let { transform(it) } // Chỉ gọi transform nếu data không null
                    emit(Result.Success(Unit)) // Luôn trả về Unit
                }
                is Result.Error -> emit(Result.Error(result.exception, result.message))
            }
        }
    }.flowOn(Dispatchers.IO)
}