package com.module.domain.api.interfaces

import com.module.core.network.model.BaseResponse
import com.module.domain.api.model.CreateOrderRequest
import com.module.domain.api.model.MergeOrderRequest
import com.module.domain.api.model.OrderInfoResponse
import com.module.domain.api.model.OrderResponse
import com.module.domain.api.model.SplitOrderRequest
import com.module.domain.api.model.SplitOrderResponse
import com.module.domain.api.model.MergeOrderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApiInterface {
    @POST("orders/create-order")
    suspend fun createOrder(@Body request: CreateOrderRequest): BaseResponse<OrderResponse>

    @GET("orders/order-info")
    suspend fun getOrderInfo(@Query("table_id") tableId: String): BaseResponse<OrderInfoResponse>

    @POST("orders/split-order")
    suspend fun splitOrder(@Body request: SplitOrderRequest): BaseResponse<SplitOrderResponse>

    @POST("orders/merge-order")
    suspend fun mergeOrder(@Body request: MergeOrderRequest): BaseResponse<MergeOrderResponse>
}