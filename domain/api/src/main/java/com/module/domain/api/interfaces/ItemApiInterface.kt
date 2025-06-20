package com.module.domain.api.interfaces

import com.module.core.network.model.BaseResponse
import com.module.domain.api.model.Category
import com.module.domain.api.model.CreateCategoryRequest
import com.module.domain.api.model.CreateItemBannerRequest
import com.module.domain.api.model.CreateItemRequest
import com.module.domain.api.model.DeleteCategoryRequest
import com.module.domain.api.model.DeleteItemRequest
import com.module.domain.api.model.Item
import com.module.domain.api.model.ItemBanner
import com.module.domain.api.model.UpdateItemRequest
import retrofit2.http.*

interface ItemApiInterface {
    @GET("/item")
    suspend fun getAllItems(): BaseResponse<List<Item>>

    @GET("/item/categories")
    suspend fun getAllCategories(): BaseResponse<List<Category>>

    @GET("/item/item-banner")
    suspend fun getItemBanner(): BaseResponse<List<ItemBanner>>

    @POST("/item/create-itembanner")
    suspend fun createItemBanner(@Body request: CreateItemBannerRequest): BaseResponse<ItemBanner>

    @POST("/item/create-category")
    suspend fun createCategory(@Body request: CreateCategoryRequest): BaseResponse<Category>

    @DELETE("/item/delete-category")
    suspend fun deleteCategory(@Body request: DeleteCategoryRequest): BaseResponse<Unit>

    @POST("/item/create-item")
    suspend fun createItem(@Body request: CreateItemRequest): BaseResponse<Item>

    @PATCH("/item/update-item")
    suspend fun updateItem(@Body request: UpdateItemRequest): BaseResponse<Item>

    @GET("/item/search-item")
    suspend fun searchItem(@Query("name") name: String): BaseResponse<List<Item>>

    @DELETE("/item/delete-item")
    suspend fun deleteItem(@Body request: DeleteItemRequest): BaseResponse<Unit>

    @GET("/item/filter-by-category")
    suspend fun filterItemsByCategory(@Query("categoryId") categoryId: String): BaseResponse<List<Item>>
}