package com.module.domain.api.repository

import com.module.core.network.model.BaseResponse
import com.module.core.network.model.Result
import com.module.domain.api.interfaces.TableApiInterface
import com.module.domain.api.model.Table
import com.module.domain.api.model.TableStatus
import com.module.domain.api.model.TableResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface TableRepository {
    suspend fun getAllTables(): Flow<Result<List<Table>>>
    suspend fun getAllTableStatuses(): Flow<Result<List<TableStatus>>>
    suspend fun getAvailableTables(startTime: String, endTime: String): Flow<Result<List<Table>>>
    suspend fun getTableById(tableId: String): Flow<Result<TableResponse>>
}

class TableRepositoryImpl @Inject constructor(
    private val tableApiInterface: TableApiInterface
) : TableRepository {

    override suspend fun getAllTables() = flow {
        Log.d("TableRepository", "Fetching all tables")
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<Table>> = tableApiInterface.getAllTables()
            Log.d("TableRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("TableRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("TableRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("TableRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllTableStatuses() = flow {
        Log.d("TableRepository", "Fetching all table statuses")
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<TableStatus>> = tableApiInterface.getAllTableStatuses()
            Log.d("TableRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("TableRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("TableRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("TableRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAvailableTables(startTime: String, endTime: String) = flow {
        Log.d("TableRepository", "Fetching available tables for startTime=$startTime, endTime=$endTime")
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<Table>> = tableApiInterface.getAvailableTables(startTime, endTime)
            Log.d("TableRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("TableRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("TableRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("TableRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getTableById(tableId: String) = flow {
        Log.d("TableRepository", "Fetching table by id: $tableId")
        try {
            emit(Result.Loading)
            val response: BaseResponse<TableResponse> = tableApiInterface.getTableById(tableId)
            Log.d("TableRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("TableRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("TableRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("TableRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)
}