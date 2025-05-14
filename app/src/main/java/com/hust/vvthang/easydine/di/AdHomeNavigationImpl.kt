package com.hust.vvthang.easydine.di

import android.os.Bundle
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.navigation.AdHomeNavigation
import com.hust.vvthang.easydine.navigation.AppNavigation
import com.module.admin.area.AreaNavigation
import com.module.core.navigation.BaseNavigatorImpl
import com.module.features.login.ui.LoginNavigation
import dagger.hilt.android.scopes.ActivityScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class AdHomeNavigationImpl @Inject constructor() : BaseNavigatorImpl(), AppNavigation, LoginNavigation, AreaNavigation, AdHomeNavigation {

    override fun openLoginToAdminHome(bundle: Bundle?) {
        Timber.d("Navigating to AdHomeFragment")
        goTo(R.id.action_loginFragment_to_adHomeFragment, bundle)
    }

    override fun openLoginToUserHome(bundle: Bundle?) {
        Timber.d("Navigating to UserHomeFragment")
        goTo(R.id.action_loginFragment_to_userHomeFragment, bundle)
    }

    override fun openAreaToSales(bundle: Bundle?) {
        Timber.d("Navigating from AreaFragment to SalesFragment")
        try {
            goTo(R.id.action_areaFragment_to_salesFragment, bundle)
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Failed to navigate from AreaFragment to SalesFragment")
        }
    }
}