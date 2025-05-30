package com.module.domain.api.repository

import com.module.core.network.model.BaseResponse
import com.module.core.network.model.Result
import com.module.domain.api.interfaces.OrderApiInterface
import com.module.domain.api.model.CreateOrderRequest
import com.module.domain.api.model.MergeOrderRequest
import com.module.domain.api.model.OrderInfoResponse
import com.module.domain.api.model.OrderResponse
import com.module.domain.api.model.SplitOrderRequest
import com.module.domain.api.model.SplitOrderResponse
import com.module.domain.api.model.MergeOrderResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface OrderRepository {
    suspend fun createOrder(request: CreateOrderRequest): Flow<Result<OrderResponse>>
    suspend fun getOrderInfo(tableId: String): Flow<Result<OrderInfoResponse>>
    suspend fun splitOrder(request: SplitOrderRequest): Flow<Result<SplitOrderResponse>>
    suspend fun mergeOrder(request: MergeOrderRequest): Flow<Result<MergeOrderResponse>>
}

class OrderRepositoryImpl @Inject constructor(
    private val orderApiInterface: OrderApiInterface
) : OrderRepository {

    override suspend fun createOrder(request: CreateOrderRequest) = flow {
        try {
            emit(Result.Loading)
            val response: BaseResponse<OrderResponse> = orderApiInterface.createOrder(request)
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getOrderInfo(tableId: String) = flow {
        try {
            emit(Result.Loading)
            val response: BaseResponse<OrderInfoResponse> = orderApiInterface.getOrderInfo(tableId)
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun splitOrder(request: SplitOrderRequest) = flow {
        try {
            emit(Result.Loading)
            val response: BaseResponse<SplitOrderResponse> = orderApiInterface.splitOrder(request)
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun mergeOrder(request: MergeOrderRequest) = flow {
        try {
            emit(Result.Loading)
            val response: BaseResponse<MergeOrderResponse> = orderApiInterface.mergeOrder(request)
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)
}