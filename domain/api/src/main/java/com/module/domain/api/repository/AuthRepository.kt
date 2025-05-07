package com.module.domain.api.repository

import com.module.core.network.model.Result
import com.module.domain.api.interfaces.AuthApiInterface
import com.module.domain.api.model.Login
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface AuthRepository {
    suspend fun login(request: Login.Request): Flow<Result<Login.Response>>
}

class AuthRepositoryImpl @Inject constructor(
    private val authInterface: AuthApiInterface
) : AuthRepository {

    override suspend fun login(request: Login.Request) = flow {
        try {
            emit(Result.Loading) // Phát ra trạng thái Loading
            val response = authInterface.login(request) // Gọi API
            if (response.isSuccess()) {
                emit(Result.Success(response.data)) // Phát ra kết quả thành công
            } else {
                emit(Result.Error(Exception(response.message), response.message)) // Lỗi từ API
            }
        } catch (e: HttpException) {
            // Xử lý lỗi HTTP (401, 403, 500, ...)
            emit(Result.Error(e, e.message()))
        } catch (e: Exception) {
            // Xử lý các lỗi khác (mạng, parsing, ...)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)
}