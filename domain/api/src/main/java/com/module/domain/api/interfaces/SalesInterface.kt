package com.module.domain.api.interfaces

import com.module.domain.api.model.AdminHome
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface SalesApiInterface {
    @GET("item/categories")
    suspend fun getCategories(): List<AdminHome.Category>

    @GET("item/search-item")
    suspend fun getItemsByCriteria(@QueryMap criteria: Map<String, String>): AdminHome.ItemResponse
}