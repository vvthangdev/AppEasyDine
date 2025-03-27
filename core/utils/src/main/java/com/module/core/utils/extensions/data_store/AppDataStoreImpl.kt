package com.module.core.utils.extensions.data_store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.module.core.utils.extensions.constants.PreferenceKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(PreferenceKey.APP_DATASTORE)

@Singleton
class AppDataStoreImpl @Inject constructor(
    @ApplicationContext val context: Context
) : AppDataStore {

    private val mDStore: DataStore<Preferences>
        get() = context.dataStore

    private inline fun <reified T> getKey(key: String): Preferences.Key<T> {
        return when (T::class) {
            String::class -> stringPreferencesKey(key)
            Int::class -> intPreferencesKey(key)
            Boolean::class -> booleanPreferencesKey(key)
            Long::class -> longPreferencesKey(key)
            Double::class -> doublePreferencesKey(key)
            Float::class -> floatPreferencesKey(key)
            else -> throw IllegalArgumentException("Unsupported type")
        } as Preferences.Key<T>
    }

    override suspend fun put(key: String, value: String) {
        val storeKey = getKey<String>(key)
        mDStore.edit { it[storeKey] = value }
    }

    override suspend fun put(key: String, value: Int) {
        val storeKey = getKey<Int>(key)
        mDStore.edit { it[storeKey] = value }
    }

    override suspend fun put(key: String, value: Long) {
        val storeKey = getKey<Long>(key)
        mDStore.edit { it[storeKey] = value }
    }

    override suspend fun put(key: String, value: Double) {
        val storeKey = getKey<Double>(key)
        mDStore.edit { it[storeKey] = value }
    }

    override suspend fun put(key: String, value: Float) {
        val storeKey = getKey<Float>(key)
        mDStore.edit { it[storeKey] = value }
    }

    override suspend fun put(key: String, value: Boolean) {
        val storeKey = getKey<Boolean>(key)
        mDStore.edit { it[storeKey] = value }
    }

    override suspend fun get(key: String, defaultValue: String?): Flow<String?> {
        val storeKey = getKey<String>(key)
        return mDStore.data.map { it[storeKey] ?: defaultValue }
    }

    override suspend fun get(key: String, defaultValue: Int?): Flow<Int?> {
        val storeKey = getKey<Int>(key)
        return mDStore.data.map { it[storeKey] ?: defaultValue }
    }

    override suspend fun get(key: String, defaultValue: Long?): Flow<Long?> {
        val storeKey = getKey<Long>(key)
        return mDStore.data.map { it[storeKey] ?: defaultValue }
    }

    override suspend fun get(key: String, defaultValue: Double?): Flow<Double?> {
        val storeKey = getKey<Double>(key)
        return mDStore.data.map { it[storeKey] ?: defaultValue }
    }

    override suspend fun get(key: String, defaultValue: Float?): Flow<Float?> {
        val storeKey = getKey<Float>(key)
        return mDStore.data.map { it[storeKey] ?: defaultValue }
    }

    override suspend fun get(key: String, defaultValue: Boolean?): Flow<Boolean?> {
        val storeKey = getKey<Boolean>(key)
        return mDStore.data.map { it[storeKey] ?: defaultValue }
    }
}