package com.module.core.navigation

import android.os.Bundle

interface CoreNavigation: BaseNavigator {
    fun openAreaToSales(bundle: Bundle? = null)
    fun openSplashFragment(bundle: Bundle? = null)
    fun openProfileToEditProfile(bundle: Bundle? = null)
    fun openSaleToCart(bundle: Bundle? = null)
    fun openSalesFragment(bundle: Bundle? = null)
//    fun openLoginScreen(bundle: Bundle? = null)
//    fun openProfileToEditProfile(bundle: Bundle? = null)
}