package com.hust.vvthang.easydine.di

import android.os.Bundle
import com.hust.vvthang.easydine.R
import com.module.core.navigation.BaseNavigatorImpl
import com.module.core.navigation.CoreNavigation
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class CoreNavigationImpl @Inject constructor() : BaseNavigatorImpl(), CoreNavigation {
    override fun openAreaToSales(bundle: Bundle?) {
            goTo(R.id.action_areaFragment_to_salesFragment, bundle)
    }

    override fun openSaleToCart(bundle: Bundle?) {
        goTo(R.id.action_salesFragment_to_cartFragment, bundle)
    }
}