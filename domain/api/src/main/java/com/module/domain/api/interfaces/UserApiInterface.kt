package com.module.domain.api.interfaces

import com.module.core.network.model.BaseResponse
import com.module.domain.api.model.*
import retrofit2.http.*

interface UserApiInterface {
    @POST("users/signup")
    suspend fun signUp(@Body request: SignUpRequest): BaseResponse<SignUpResponse>

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<LoginResponse>

    @POST("users/logout")
    suspend fun logout(): BaseResponse<Nothing?>

    @GET("users/all-users")
    suspend fun getAllUsers(): BaseResponse<List<User>>

    @POST("users/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): BaseResponse<RefreshTokenResponse>

    @PATCH("users/update-user")
    suspend fun updateUser(@Body request: UpdateUserRequest): BaseResponse<Nothing?>

    @DELETE("users/delete")
    suspend fun deleteUser(@Body request: DeleteUserRequest): BaseResponse<Nothing?>

    @GET("users/user-info")
    suspend fun getUserInfo(): BaseResponse<User>

    @POST("users/auth/google/firebase")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): BaseResponse<LoginResponse>
}