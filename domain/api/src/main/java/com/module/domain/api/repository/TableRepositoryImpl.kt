package com.module.domain.api.repository

import com.module.core.network.model.BaseResponse
import com.module.core.network.model.Result
import com.module.domain.api.interfaces.TableApiInterface
import com.module.domain.api.model.Table
import com.module.domain.api.model.TableStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface TableRepository {
    suspend fun getAllTables(): Flow<Result<List<Table>>>
    suspend fun getAllTableStatuses(): Flow<Result<List<TableStatus>>>
}

class TableRepositoryImpl @Inject constructor(
    private val tableApiInterface: TableApiInterface
) : TableRepository {

    override suspend fun getAllTables() = flow {
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<Table>> = tableApiInterface.getAllTables()
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllTableStatuses() = flow {
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<TableStatus>> = tableApiInterface.getAllTableStatuses()
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)
}