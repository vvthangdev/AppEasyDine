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
import timber.log.Timber
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
    suspend fun logout(): Flow<Result<Unit>>
    suspend fun googleLogin(request: GoogleLoginRequest): Flow<Result<LoginResponse>>
}

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApiInterface: UserApiInterface
) : UserRepository {

    private suspend fun <T> safeApiCall(
        logMessage: String,
        apiCall: suspend () -> BaseResponse<T>
    ): Flow<Result<T>> = flow {
        Timber.d(logMessage)
        try {
            emit(Result.Loading)
            val response = apiCall()
            Timber.d("Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Timber.e("API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Timber.e(e, "HTTP error: ${e.code()} - ${e.message}")
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error: ${e.message}")
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun signUp(request: SignUpRequest) = safeApiCall("Signing up user: ${request.username}") {
        userApiInterface.signUp(request)
    }

    override suspend fun login(request: LoginRequest) = safeApiCall("Logging in user: ${request.email}") {
        userApiInterface.login(request)
    }

    override suspend fun getAllUsers() = safeApiCall("Fetching all users") {
        userApiInterface.getAllUsers()
    }

    override suspend fun refreshToken(request: RefreshTokenRequest) = safeApiCall("Refreshing token with refreshToken: ${request.refreshToken}") {
        userApiInterface.refreshToken(request)
    }

    override suspend fun updateUser(request: UpdateUserRequest) = safeApiCall("Updating user: $request") {
        userApiInterface.updateUser(request)
    }.mapSuccess { Unit }

    override suspend fun deleteUser(request: DeleteUserRequest) = safeApiCall("Deleting user") {
        userApiInterface.deleteUser(request)
    }.mapSuccess { Unit }

    override suspend fun getUserInfo() = safeApiCall("Fetching user info") {
        userApiInterface.getUserInfo()
    }

    override suspend fun logout() = safeApiCall("Logging out user") {
        userApiInterface.logout()
    }.mapSuccess { Unit }

    override suspend fun googleLogin(request: GoogleLoginRequest) = safeApiCall("Logging in with Google: ${request.idToken.take(10)}...") {
        userApiInterface.googleLogin(request)
    }

    private fun <T> Flow<Result<T>>.mapSuccess(transform: (T) -> Unit): Flow<Result<Unit>> = flow {
        collect { result ->
            when (result) {
                is Result.Loading -> emit(Result.Loading)
                is Result.Success -> {
                    result.data?.let { transform(it) }
                    emit(Result.Success(Unit))
                }
                is Result.Error -> emit(Result.Error(result.exception, result.message))
            }
        }
    }.flowOn(Dispatchers.IO)
}