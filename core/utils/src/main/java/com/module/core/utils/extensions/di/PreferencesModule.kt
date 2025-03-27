package com.module.core.utils.extensions.di

import com.module.core.utils.extensions.data_store.AppDataStore
import com.module.core.utils.extensions.data_store.AppDataStoreImpl
import com.module.core.utils.extensions.shared_preferences.AppPreferences
import com.module.core.utils.extensions.shared_preferences.AppPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    abstract fun provideAppPreferences(preferencesImpl: AppPreferencesImpl): AppPreferences

    @Binds
    abstract fun provideAppDatastore(dataStoreImpl: AppDataStoreImpl): AppDataStore
}