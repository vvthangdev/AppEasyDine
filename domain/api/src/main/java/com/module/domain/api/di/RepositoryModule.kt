package com.module.domain.api.di

import com.module.domain.api.repository.AuthRepository
import com.module.domain.api.repository.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideAuthRepository(repositoryImpl: AuthRepositoryImpl): AuthRepository
}