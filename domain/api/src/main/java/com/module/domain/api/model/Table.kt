package com.module.domain.api.model

import com.google.gson.annotations.SerializedName

data class Table(
    @SerializedName("_id") val id: String,
    @SerializedName("table_number") val tableNumber: Int,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("area") val area: String,
    @SerializedName("__v") val version: Int
)

data class TableStatus(
    @SerializedName("table_id") val tableId: String,
    @SerializedName("table_number") val tableNumber: Int,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("area") val area: String,
    @SerializedName("status") val status: String,
    @SerializedName("start_time") val startTime: String?,
    @SerializedName("end_time") val endTime: String?,
    @SerializedName("reservation_id") val reservationId: String?
)

data class TableResponse(
    @SerializedName("table_id") val tableId: String,
    @SerializedName("table_number") val tableNumber: Int,
    @SerializedName("capacity") val capacity: Int,
    @SerializedName("area") val area: String
)
