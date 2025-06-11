package com.module.domain.api.model

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("staff_id") val staffId: String?,
    @SerializedName("cashier_id") val cashierId: String?,
    @SerializedName("time") val time: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("voucher_code") val voucherCode: String?,
    @SerializedName("total_amount") val totalAmount: Long,
    @SerializedName("discount_amount") val discountAmount: Long,
    @SerializedName("final_amount") val finalAmount: Long,
    @SerializedName("payment_method") val paymentMethod: String?,
    @SerializedName("payment_status") val paymentStatus: String,
    @SerializedName("transaction_id") val transactionId: String?,
    @SerializedName("vnp_transaction_no") val vnpTransactionNo: String?,
    @SerializedName("star") val star: Int?,
    @SerializedName("comment") val comment: String?,
    @SerializedName("voucher_id") val voucherId: String?,
    @SerializedName("tables") val tables: List<String>?,
    @SerializedName("__v") val version: Int
)

data class OrderInfoResponse(
    @SerializedName("order") val order: OrderResponse,
    @SerializedName("reservedTables") val reservedTables: List<ReservedTable>,
    @SerializedName("itemOrders") val itemOrders: List<ItemOrder>
)

data class ReservedTable(
    @SerializedName("table_id") val tableId: String,
    @SerializedName("table_number") val tableNumber: Int,
    @SerializedName("area") val area: String,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("status") val status: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String
)

data class ItemOrder(
    @SerializedName("_id") val id: String,
    @SerializedName("item_id") val itemId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("order_id") val orderId: String,
    @SerializedName("size") val size: String?,
    @SerializedName("note") val note: String?,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("itemImage") val itemImage: String?,
    @SerializedName("itemPrice") val itemPrice: Long
)

data class CreateOrderRequest(
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("tables") val tables: List<String>,
    @SerializedName("items") val items: List<OrderItemRequest>,
)

data class OrderItemRequest(
    @SerializedName("id") val id: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("size") val size: String?,
    @SerializedName("note") val note: String?
)

data class UpdateOrderRequest(
    @SerializedName("id") val id: String,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("tables") val tables: List<String>?,
    @SerializedName("items") val items: List<OrderItemRequest>?,
    @SerializedName("customer_id") val customerId: String?,
    @SerializedName("staff_id") val staffId: String?,
    @SerializedName("voucherCode") val voucherCode: String?,
)

// Trong file com.module.domain.api.model (thêm vào cùng file với các model khác)
data class ReserveTableRequest(
    @SerializedName("start_time") val startTime: String,
    @SerializedName("people_assigned") val peopleAssigned: Int,
    @SerializedName("items") val items: List<OrderItemRequest>? = null,
    @SerializedName("status") val status: String? = "pending"
)

data class ConfirmOrderRequest(
    @SerializedName("order_id") val orderId: String
)

data class SplitOrderRequest(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("new_items") val newItems: List<OrderItemRequest>
)

data class SplitOrderResponse(
    @SerializedName("originalOrder") val originalOrder: OrderResponse,
    @SerializedName("newOrder") val newOrder: OrderResponse
)

data class MergeOrderRequest(
    @SerializedName("source_order_id") val sourceOrderId: String,
    @SerializedName("target_order_id") val targetOrderId: String
)

data class MergeOrderResponse(
    @SerializedName("order") val order: OrderResponse
)

data class AddItemsToOrderRequest(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("items") val items: List<OrderItemRequest>
)
