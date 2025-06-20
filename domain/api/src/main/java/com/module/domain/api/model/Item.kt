package com.module.domain.api.model

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Long,
    @SerializedName("image") val image: String?,
    @SerializedName("categories") val categories: List<Category>?,
    @SerializedName("sizes") val sizes: List<Size>?,
    @SerializedName("nameNoAccents") val nameNoAccents: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("__v") val version: Int
)

data class Category(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("__v") val version: Int
)

data class Size(
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Long
)

data class ItemBanner(
    @SerializedName("_id") val id: String,
    @SerializedName("image") val image: String,
    @SerializedName("title") val title: String,
    @SerializedName("__v") val version: Int
)

data class CreateItemBannerRequest(
    @SerializedName("image") val image: String,
    @SerializedName("title") val title: String
)

data class CreateCategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("image") val image: String? = null
)

data class DeleteCategoryRequest(
    @SerializedName("categoryId") val categoryId: String
)

data class CreateItemRequest(
    @SerializedName("name") val name: String,
    @SerializedName("price") val price: Long,
    @SerializedName("description") val description: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("categories") val categories: List<String>? = null,
    @SerializedName("sizes") val sizes: List<Size>? = null
)

data class UpdateItemRequest(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("price") val price: Long? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("categories") val categories: List<String>? = null,
    @SerializedName("sizes") val sizes: List<Size>? = null
)

data class DeleteItemRequest(
    @SerializedName("id") val id: String
)

data class CartItem(
    val item: Item,
    var quantity: Int,
    var selectedSize: Size? = null,
    var note: String? = null
) {
    val uniqueKey: String
        get() = "${item.id}_${selectedSize?.name ?: "no_size"}"
}