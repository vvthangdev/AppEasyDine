package com.module.domain.api.repository

import com.module.core.network.model.BaseResponse
import com.module.core.network.model.Result
import com.module.domain.api.interfaces.ItemApiInterface
import com.module.domain.api.model.Category
import com.module.domain.api.model.CreateCategoryRequest
import com.module.domain.api.model.CreateItemBannerRequest
import com.module.domain.api.model.CreateItemRequest
import com.module.domain.api.model.DeleteCategoryRequest
import com.module.domain.api.model.DeleteItemRequest
import com.module.domain.api.model.Item
import com.module.domain.api.model.ItemBanner
import com.module.domain.api.model.UpdateItemRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
interface ItemRepository {
    suspend fun getAllItems(): Flow<Result<List<Item>>>
    suspend fun getAllCategories(): Flow<Result<List<Category>>>
    suspend fun getItemBanner(): Flow<Result<List<ItemBanner>>>
    suspend fun createItemBanner(request: CreateItemBannerRequest): Flow<Result<ItemBanner>>
    suspend fun createCategory(request: CreateCategoryRequest): Flow<Result<Category>>
    suspend fun deleteCategory(request: DeleteCategoryRequest): Flow<Result<Unit>>
    suspend fun createItem(request: CreateItemRequest): Flow<Result<Item>>
    suspend fun updateItem(request: UpdateItemRequest): Flow<Result<Item>>
    suspend fun searchItem(name: String): Flow<Result<List<Item>>>
    suspend fun deleteItem(request: DeleteItemRequest): Flow<Result<Unit>>
    suspend fun filterItemsByCategory(categoryId: String): Flow<Result<List<Item>>>
}

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemApiInterface: ItemApiInterface
) : ItemRepository {

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

    override suspend fun getAllItems() = safeApiCall("Fetching all items") {
        itemApiInterface.getAllItems()
    }

    override suspend fun getAllCategories() = safeApiCall("Fetching all categories") {
        itemApiInterface.getAllCategories()
    }

    override suspend fun getItemBanner() = safeApiCall("Fetching item banners") {
        itemApiInterface.getItemBanner()
    }

    override suspend fun createItemBanner(request: CreateItemBannerRequest) = safeApiCall("Creating item banner: $request") {
        itemApiInterface.createItemBanner(request)
    }

    override suspend fun createCategory(request: CreateCategoryRequest) = safeApiCall("Creating category: $request") {
        itemApiInterface.createCategory(request)
    }

    override suspend fun deleteCategory(request: DeleteCategoryRequest) = safeApiCall("Deleting category: ${request.categoryId}") {
        itemApiInterface.deleteCategory(request)
    }.mapSuccess { Unit }

    override suspend fun createItem(request: CreateItemRequest) = safeApiCall("Creating item: $request") {
        itemApiInterface.createItem(request)
    }

    override suspend fun updateItem(request: UpdateItemRequest) = safeApiCall("Updating item: ${request.id}") {
        itemApiInterface.updateItem(request)
    }

    override suspend fun searchItem(name: String) = safeApiCall("Searching items with name: $name") {
        itemApiInterface.searchItem(name)
    }

    override suspend fun deleteItem(request: DeleteItemRequest) = safeApiCall("Deleting item: ${request.id}") {
        itemApiInterface.deleteItem(request)
    }.mapSuccess { Unit }

    override suspend fun filterItemsByCategory(categoryId: String) = safeApiCall("Filtering items by category: $categoryId") {
        itemApiInterface.filterItemsByCategory(categoryId)
    }

    // Hàm hỗ trợ để chuyển đổi Result.Success<T> thành Result.Success<Unit>
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