package com.hust.vvthang.easydine.di

import android.os.Bundle
import com.hust.vvthang.easydine.R
import com.module.admin.profile.ProfileNavigator
import com.module.admin.sale.SaleNavigation
import com.module.core.navigation.BaseNavigatorImpl
import com.module.core.navigation.CoreNavigation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class CoreNavigationImpl @Inject constructor() : BaseNavigatorImpl(), CoreNavigation, ProfileNavigator, SaleNavigation {
    override fun openAreaToSales(bundle: Bundle?) {
//            goTo(R.id.ac, bundle)
    }

//    override fun openSaleToCart(bundle: Bundle?) {
//        goTo(R.id.action_adHomeFragment_to_cartFragment, bundle)
//    }

    override fun openSaleToCart(bundle: Bundle?) {
        goTo(R.id.action_to_cartFragment, bundle)
    }

    override fun openSplashFragment(bundle: Bundle?) {
        popBackStack(R.id.login_nav) // Pop login_nav
        goTo(R.id.action_to_splashFragment, bundle) // Navigate to SplashFragment
    }

    override fun openProfileToEditProfile(bundle: Bundle?) {
        goTo(R.id.action_adHomeFragment_to_editProfileFragment, bundle)
    }
}