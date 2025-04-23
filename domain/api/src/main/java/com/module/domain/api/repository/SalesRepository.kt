package com.module.domain.api.repository

import com.module.core.network.model.Result
import com.module.domain.api.interfaces.SalesApiInterface
import com.module.domain.api.model.AdminHome
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface SaleRepository {
    suspend fun getCategories(): Flow<Result<List<AdminHome.Category>>>

    //    suspend fun getItemsByCriteria(categoryId: String): Flow<Result<List<AdminHome.Item>>>
    suspend fun getItemsByCriteria(criteria: Map<String, String>): Flow<Result<List<AdminHome.Item>>>
}

class SalesRepositoryImpl @Inject constructor(
    private val salesInterface: SalesApiInterface
) : SaleRepository {
    override suspend fun getCategories(): Flow<Result<List<AdminHome.Category>>> = flow {
        emit(Result.Loading())
        try {
            val response = salesInterface.getCategories()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun getItemsByCriteria(criteria: Map<String, String>): Flow<Result<List<AdminHome.Item>>> =
        flow {
            emit(Result.Loading())
            try {
                val response = salesInterface.getItemsByCriteria(criteria) // Truyền map criteria
                emit(Result.Success(response.item))
            } catch (e: Exception) {
                emit(Result.Error(e))
            }
        }
}