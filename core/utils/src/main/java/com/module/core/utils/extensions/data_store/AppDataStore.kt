package com.module.core.utils.extensions.data_store

import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface AppDataStore {

    suspend fun put(key: String, value: String)

    suspend fun get(key: String, defaultValue: String? = null): Flow<String?>

    suspend fun put(key: String, value: Int)

    suspend fun get(key: String, defaultValue: Int? = null): Flow<Int?>

    suspend fun put(key: String, value: Long)

    suspend fun get(key: String, defaultValue: Long? = null): Flow<Long?>

    suspend fun put(key: String, value: Double)

    suspend fun get(key: String, defaultValue: Double? = null): Flow<Double?>

    suspend fun put(key: String, value: Float)

    suspend fun get(key: String, defaultValue: Float? = null): Flow<Float?>

    suspend fun put(key: String, value: Boolean)

    suspend fun get(key: String, defaultValue: Boolean? = null): Flow<Boolean?>
}