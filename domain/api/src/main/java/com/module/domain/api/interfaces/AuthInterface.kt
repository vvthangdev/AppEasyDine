package com.module.domain.api.interfaces

import com.module.core.network.model.BaseResponse
import com.module.domain.api.model.Login
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiInterface {
    @POST("/users/login")
    suspend fun login(@Body request: Login.Request): BaseResponse<Login.Response>
}