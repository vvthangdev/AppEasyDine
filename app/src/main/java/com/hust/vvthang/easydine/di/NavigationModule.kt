package com.hust.vvthang.easydine.di

import com.hust.vvthang.easydine.navigation.AppNavigation
import com.hust.vvthang.easydine.navigation.AppNavigatorImpl
import com.module.core.navigation.BaseNavigator
import com.module.features.login.ui.LoginNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class NavigationModule {

    @Binds
    abstract fun provideBaseNavigator(navigator: AppNavigatorImpl): BaseNavigator

    @Binds
    abstract fun provideLoginNavigation(navigator: AppNavigatorImpl): LoginNavigation

    @Binds
    abstract fun provideAppNavigator(navigator: AppNavigatorImpl): AppNavigation
}