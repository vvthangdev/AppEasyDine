package com.module.domain.api.model

class AdminHome {
    // Category.kt
    data class Category(
        val _id: String,
        val name: String,
        val description: String,
        val createdAt: String,
        val updatedAt: String,
        val __v: Int
    )

    // Item.kt
    data class Item(
        val _id: String,
        val name: String,
        val image: String,
        val price: Long,
        val categories: List<Category>,
        val __v: Int
    )

    data class ItemResponse(val item: List<Item>)
}

