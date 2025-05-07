package com.module.domain.api.model

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Long,
    @SerializedName("image") val image: String?,
    @SerializedName("categories") val categories: List<Category>,
    @SerializedName("sizes") val sizes: List<Size>,
    @SerializedName("nameNoAccents") val nameNoAccents: String,
    @SerializedName("description") val description: String?,
    @SerializedName("__v") val version: Int
)

data class Category(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("__v") val version: Int
)

data class Size(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Long
)

data class CartItem(
    val item: Item,
    var quantity: Int
)