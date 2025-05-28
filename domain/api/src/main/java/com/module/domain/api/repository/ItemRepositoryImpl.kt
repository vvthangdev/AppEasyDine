package com.module.domain.api.repository

import com.module.core.network.model.BaseResponse
import com.module.core.network.model.Result
import com.module.domain.api.interfaces.ItemApiInterface
import com.module.domain.api.model.Item
import com.module.domain.api.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface ItemRepository {
    suspend fun getAllItems(): Flow<Result<List<Item>>>
    suspend fun getAllCategories(): Flow<Result<List<Category>>>
}

class ItemRepositoryImpl @Inject constructor(
    private val itemApiInterface: ItemApiInterface
) : ItemRepository {

    override suspend fun getAllItems() = flow {
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<Item>> = itemApiInterface.getAllItems()
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

    override suspend fun getAllCategories() = flow {
        try {
            emit(Result.Loading)
            val response: BaseResponse<List<Category>> = itemApiInterface.getAllCategories()
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