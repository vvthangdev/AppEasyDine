package com.module.domain.api.interfaces

import com.module.core.network.model.BaseResponse
import com.module.domain.api.model.*
import retrofit2.http.*

interface OrderApiInterface {
    @GET("orders")
    suspend fun getAllOrders(): BaseResponse<List<OrderResponse>>

    @POST("orders/create-order")
    suspend fun createOrder(@Body request: CreateOrderRequest): BaseResponse<Any?>

    @GET("orders/order-info")
    suspend fun getOrderInfo(
        @Query("id") orderId: String?,
        @Query("table_id") tableId: String?
    ): BaseResponse<OrderInfoResponse>

    @POST("orders/confirm-order")
    suspend fun confirmOrder(@Body request: ConfirmOrderRequest): BaseResponse<Any?>

    @POST("orders/split-order")
    suspend fun splitOrder(@Body request: SplitOrderRequest): BaseResponse<Any?>

    @POST("orders/merge-order")
    suspend fun mergeOrder(@Body request: MergeOrderRequest): BaseResponse<Any?>

    @PATCH("orders/update-order")
    suspend fun updateOrder(@Body request: UpdateOrderRequest): BaseResponse<Any?>

    @POST("orders/add-items-to-order")
    suspend fun addItemsToOrder(@Body request: AddItemsToOrderRequest): BaseResponse<Any?>
}