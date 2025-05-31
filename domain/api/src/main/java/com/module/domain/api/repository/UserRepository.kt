package com.module.domain.api.repository

import com.module.core.network.model.BaseResponse
import com.module.core.network.model.Result
import com.module.domain.api.interfaces.UserApiInterface
import com.module.domain.api.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface UserRepository {
    suspend fun signUp(request: SignUpRequest): Flow<Result<SignUpResponse>>
    suspend fun login(request: LoginRequest): Flow<Result<LoginResponse>>
    suspend fun getAllUsers(): Flow<Result<List<User>>>
    suspend fun refreshToken(request: RefreshTokenRequest): Flow<Result<RefreshTokenResponse>>
    suspend fun updateUser(request: UpdateUserRequest): Flow<Result<Unit>>
    suspend fun deleteUser(request: DeleteUserRequest): Flow<Result<Unit>>
    suspend fun getUserInfo(): Flow<Result<User>>
}

class UserRepositoryImpl @Inject constructor(
    private val userApiInterface: UserApiInterface
) : UserRepository {

    override suspend fun signUp(request: SignUpRequest) = flow {
        Log.d("UserRepository", "Signing up user: ${request.username}")
        try {
            emit(Result.Loading)
            val response: BaseResponse<SignUpResponse> = userApiInterface.signUp(request)
            Log.d("UserRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("UserRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun login(request: LoginRequest) = flow {
        Log.d("UserRepository", "Logging in user: ${request.email}")
        try {
            emit(Result.Loading)
            val response: BaseResponse<LoginResponse> = userApiInterface.login(request)
            Log.d("UserRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("UserRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllUsers() = flow {
        Log.d("UserRepository", "Fetching all users")
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<User>> = userApiInterface.getAllUsers()
            Log.d("UserRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("UserRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun refreshToken(request: RefreshTokenRequest) = flow {
        Log.d("UserRepository", "Refreshing token with refreshToken: ${request.refreshToken}")
        try {
            emit(Result.Loading)
            val response: BaseResponse<RefreshTokenResponse> = userApiInterface.refreshToken(request)
            Log.d("UserRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("UserRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun updateUser(request: UpdateUserRequest) = flow {
        Log.d("UserRepository", "Updating user: $request")
        try {
            emit(Result.Loading)
            val response: BaseResponse<Nothing?> = userApiInterface.updateUser(request)
            Log.d("UserRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(Unit))
            } else {
                Log.e("UserRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteUser(request: DeleteUserRequest) = flow {
        Log.d("UserRepository", "Deleting user")
        try {
            emit(Result.Loading)
            val response: BaseResponse<Nothing?> = userApiInterface.deleteUser(request)
            Log.d("UserRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(Unit))
            } else {
                Log.e("UserRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getUserInfo() = flow {
        Log.d("UserRepository", "Fetching user info")
        try {
            emit(Result.Loading)
            val response: BaseResponse<User> = userApiInterface.getUserInfo()
            Log.d("UserRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("UserRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("UserRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("UserRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)
}