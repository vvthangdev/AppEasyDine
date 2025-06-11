package com.hust.vvthang.easydine.di

import android.os.Bundle
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.navigation.AppNavigation
import com.module.core.navigation.BaseNavigatorImpl
import com.module.features.login.ui.LoginNavigation
import dagger.hilt.android.scopes.ActivityScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class AppNavigatorImpl @Inject constructor() : BaseNavigatorImpl(), AppNavigation, LoginNavigation {
}