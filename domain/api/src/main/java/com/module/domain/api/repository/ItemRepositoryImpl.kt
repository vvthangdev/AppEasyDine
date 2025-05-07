package com.module.domain.api.repository

import com.module.core.network.model.BaseResponse
import com.module.core.network.model.Result
import com.module.domain.api.interfaces.ItemApiInterface
import com.module.domain.api.model.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.module.domain.api.model.Category

@Singleton
interface ItemRepository {
    suspend fun getAllItems(): Flow<Result<List<Item>>>
    suspend fun getAllCategories(): Flow<Result<List<Category>>>
}

class ItemRepositoryImpl @Inject constructor(
    private val itemApiInterface: ItemApiInterface
) : ItemRepository {

    override suspend fun getAllItems() = flow {
        Log.d("ItemRepository", "Fetching all items")
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<Item>> = itemApiInterface.getAllItems()
            Log.d("ItemRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("ItemRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("ItemRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("ItemRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllCategories() = flow {
        Log.d("ItemRepository", "Fetching all categories")
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<Category>> = itemApiInterface.getAllCategories()
            Log.d("ItemRepository", "Received response: $response")
            if (response.isSuccess()) {
                emit(Result.Success(response.data))
            } else {
                Log.e("ItemRepository", "API error: ${response.message}")
                emit(Result.Error(Exception("API error: ${response.message}"), response.message))
            }
        } catch (e: HttpException) {
            Log.e("ItemRepository", "HTTP error: ${e.code()} - ${e.message}", e)
            emit(Result.Error(e, "HTTP error: ${e.message}"))
        } catch (e: Exception) {
            Log.e("ItemRepository", "Unexpected error: ${e.message}", e)
            emit(Result.Error(e, e.message))
        }
    }.flowOn(Dispatchers.IO)
}