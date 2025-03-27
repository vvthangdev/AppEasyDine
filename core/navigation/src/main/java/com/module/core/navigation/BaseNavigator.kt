package com.module.core.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController

interface BaseNavigator {

    fun bind(navController: NavController)

    fun goTo(@IdRes id: Int, bundle: Bundle? = null)

    fun goTo(route: String, bundle: Bundle? = null)

    fun back(): Boolean

    fun popBackStack(@IdRes id: Int)

    fun unbind()

}