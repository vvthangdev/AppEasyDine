package com.module.admin.area

import android.os.Bundle
import com.module.core.navigation.BaseNavigator

interface AreaNavigation : BaseNavigator {
    fun openAreaToSales(bundle: Bundle? = null)
}