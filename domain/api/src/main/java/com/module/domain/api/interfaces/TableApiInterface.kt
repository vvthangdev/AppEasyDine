package com.module.domain.api.interfaces

import com.module.core.network.model.BaseResponse
import com.module.domain.api.model.Table
import com.module.domain.api.model.TableStatus
import com.module.domain.api.model.TableResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TableApiInterface {
    @GET("tables")
    suspend fun getAllTables(): BaseResponse<List<Table>>

    @GET("tables/tables-status")
    suspend fun getAllTableStatuses(): BaseResponse<List<TableStatus>>

    @GET("tables/available-tables")
    suspend fun getAvailableTables(
        @Query("start_time") startTime: String,
        @Query("end_time") endTime: String
    ): BaseResponse<List<Table>>

    @GET("tables/get-table")
    suspend fun getTableById(@Query("table_id") tableId: String): BaseResponse<TableResponse>
}