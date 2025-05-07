package com.module.domain.api.interfaces

import com.module.core.network.model.BaseResponse
import com.module.domain.api.model.Category
import com.module.domain.api.model.Item
import retrofit2.http.GET

interface ItemApiInterface {
    @GET("item")
    suspend fun getAllItems(): BaseResponse<List<Item>>
    @GET("item/categories")
    suspend fun getAllCategories(): BaseResponse<List<Category>>
}