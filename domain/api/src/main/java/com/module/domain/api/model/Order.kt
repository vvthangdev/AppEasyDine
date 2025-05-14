package com.module.domain.api.model

import com.google.gson.annotations.SerializedName

// Request cho create-order
data class CreateOrderRequest(
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("tables") val tables: List<String>,
    @SerializedName("items") val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    @SerializedName("id") val id: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("size") val size: String?,
    @SerializedName("note") val note: String
)

// Response cho create-order
data class OrderResponse(
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("staff_id") val staffId: String?,
    @SerializedName("time") val time: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("_id") val id: String,
    @SerializedName("__v") val version: Int
)

// Response cho order-info
data class OrderInfoResponse(
    @SerializedName("order") val order: OrderDetails,
    @SerializedName("reservedTables") val reservedTables: List<ReservedTable>,
    @SerializedName("itemOrders") val itemOrders: List<ItemOrder>
)

data class OrderDetails(
    @SerializedName("id") val id: String,
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("staff_id") val staffId: String?,
    @SerializedName("time") val time: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String
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
    @SerializedName("note") val note: String,
    @SerializedName("__v") val version: Int,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("itemImage") val itemImage: String,
    @SerializedName("itemPrice") val itemPrice: Int
)

// Request cho split-order
data class SplitOrderRequest(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("new_items") val newItems: List<OrderItemRequest>
)

// Response cho split-order
data class SplitOrderResponse(
    @SerializedName("originalOrder") val originalOrder: SplitOrderDetails,
    @SerializedName("newOrder") val newOrder: SplitOrderDetails
)

data class SplitOrderDetails(
    @SerializedName("id") val id: String,
    @SerializedName("items") val items: List<SplitOrderItem>,
    @SerializedName("tables") val tables: List<ReservedTable>
)

data class SplitOrderItem(
    @SerializedName("item_id") val itemId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("size") val size: String?,
    @SerializedName("note") val note: String
)

// Request cho merge-order
data class MergeOrderRequest(
    @SerializedName("source_order_id") val sourceOrderId: String,
    @SerializedName("target_order_id") val targetOrderId: String
)

// Response cho merge-order
data class MergeOrderResponse(
    @SerializedName("order") val order: MergeOrderDetails
)

data class MergeOrderDetails(
    @SerializedName("id") val id: String,
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("staff_id") val staffId: String?,
    @SerializedName("time") val time: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("items") val items: List<MergeOrderItem>,
    @SerializedName("tables") val tables: List<ReservedTable>
)

data class MergeOrderItem(
    @SerializedName("item_id") val itemId: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("size") val size: String?,
    @SerializedName("note") val note: String,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("itemImage") val itemImage: String,
    @SerializedName("itemPrice") val itemPrice: Int
)