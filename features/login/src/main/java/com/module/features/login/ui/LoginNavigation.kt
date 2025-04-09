package com.module.features.login.ui

import android.os.Bundle
import com.module.core.navigation.BaseNavigator

interface LoginNavigation : BaseNavigator {
    fun openLoginToAdminHome(bundle: Bundle? = null)
    fun openLoginToUserHome(bundle: Bundle? = null)
}