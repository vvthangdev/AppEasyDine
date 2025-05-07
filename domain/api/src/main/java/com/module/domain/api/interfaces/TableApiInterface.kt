package com.module.domain.api.interfaces

import com.module.core.network.model.BaseResponse
import com.module.domain.api.model.Table
import com.module.domain.api.model.TableStatus
import retrofit2.http.GET

interface TableApiInterface {
    @GET("tables")
    suspend fun getAllTables(): BaseResponse<List<Table>>

    @GET("tables/tables-status")
    suspend fun getAllTableStatuses(): BaseResponse<List<TableStatus>>
}