package com.hust.vvthang.easydine.navigation

import android.os.Bundle
import com.hust.vvthang.easydine.R
import com.module.core.navigation.BaseNavigatorImpl
import com.module.features.login.ui.LoginNavigation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AppNavigatorImpl @Inject constructor() : BaseNavigatorImpl(), AppNavigation, LoginNavigation {
    override fun openLoginToHome(bundle: Bundle?) {
        goTo(R.id.action_loginFragment_to_mainHomeFragment, bundle)
    }
}

