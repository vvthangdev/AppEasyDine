package com.module.core.navigation

import android.os.Bundle

interface CoreNavigation: BaseNavigator {
    fun openAreaToSales(bundle: Bundle? = null)

    fun openSplashFragment(bundle: Bundle? = null)
//    fun openProfileToEditProfile(bundle: Bundle? = null)
}