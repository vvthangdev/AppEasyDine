package com.hust.vvthang.easydine.di

import com.hust.vvthang.easydine.navigation.AppNavigation
import com.module.core.navigation.BaseNavigator
import com.module.core.navigation.CoreNavigation
import com.module.features.login.ui.LoginNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityNavigationModule {

    @Binds
    @ActivityScoped
    abstract fun provideBaseNavigator(navigator: AppNavigatorImpl): BaseNavigator

    @Binds
    @ActivityScoped
    abstract fun provideLoginNavigation(navigator: AppNavigatorImpl): LoginNavigation

    @Binds
    @ActivityScoped
    abstract fun provideAppNavigator(navigator: AppNavigatorImpl): AppNavigation

//    @Binds
//    @ActivityScoped
//    abstract fun provideAreaNavigation(navigator: AppNavigatorImpl): AreaNavigation

//    @Binds
//    @ActivityScoped
//    abstract fun provideHomeNavigator(navigator: AppNavigatorImpl): CoreNavigation

    @Binds
    @ActivityScoped
    abstract fun provideAdHomeNavigation(navigator: CoreNavigationImpl): CoreNavigation
}