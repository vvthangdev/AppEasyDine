package com.hust.vvthang.easydine.di

import android.os.Bundle
import com.hust.vvthang.easydine.R
import com.module.admin.area.AreaNavigation
import com.module.core.navigation.AdHomeNavigation
import com.module.core.navigation.BaseNavigatorImpl
import dagger.hilt.android.scopes.ActivityScoped
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class AdHomeNavigationImpl @Inject constructor() : BaseNavigatorImpl(), AdHomeNavigation {
    override fun openAreaToSales(bundle: Bundle?) {
            goTo(R.id.action_areaFragment_to_salesFragment, bundle)
    }
}