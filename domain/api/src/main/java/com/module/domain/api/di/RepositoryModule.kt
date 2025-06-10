package com.module.domain.api.di

import com.module.domain.api.repository.AuthRepository
import com.module.domain.api.repository.AuthRepositoryImpl
import com.module.domain.api.repository.ItemRepository
import com.module.domain.api.repository.ItemRepositoryImpl
import com.module.domain.api.repository.OrderRepository
import com.module.domain.api.repository.OrderRepositoryImpl
import com.module.domain.api.repository.TableRepository
import com.module.domain.api.repository.TableRepositoryImpl
import com.module.domain.api.repository.UserRepository
import com.module.domain.api.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideAuthRepository(repositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun provideItemRepository(repositoryImpl: ItemRepositoryImpl): ItemRepository

    @Binds
    abstract fun provideTableRepository(repositoryImpl: TableRepositoryImpl): TableRepository

    @Binds
    abstract fun provideOrderRepository(repositoryImpl: OrderRepositoryImpl): OrderRepository

    @Binds
    abstract fun provideUserRepository(repositoryImpl: UserRepositoryImpl): UserRepository

}