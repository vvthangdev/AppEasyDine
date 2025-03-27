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
            emit(Result.Loading()) // Phát ra trạng thái Loading
            val response = authInterface.login(request) // Gọi API
            emit(Result.Success(response)) // Phát ra kết quả thành công
        } catch (e: HttpException) {
            // Xử lý lỗi HTTP (ví dụ: 401, 403, 500, ...)
            emit(Result.Error(e))
        } catch (e: Exception) {
            // Xử lý các lỗi khác (mạng, parsing, ...)
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

}