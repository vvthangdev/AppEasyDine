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

    override fun openLoginToAdminHome(bundle: Bundle?) {
        Timber.d("Navigating to AdHomeFragment")
        goTo(R.id.action_loginFragment_to_adHomeFragment, bundle)
    }

    override fun openLoginToUserHome(bundle: Bundle?) {
        Timber.d("Navigating to UserHomeFragment")
        goTo(R.id.action_loginFragment_to_userHomeFragment, bundle)
    }
}