package com.module.core.navigation

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import java.lang.ref.WeakReference

open class BaseNavigatorImpl : BaseNavigator {

    private var mNavController: NavController? = null

    override fun bind(navController: NavController) {
        mNavController = WeakReference(navController).get()
    }

    override fun goTo(id: Int, bundle: Bundle?) {
        mNavController?.navigate(id, bundle)
    }

    override fun goTo(route: String, bundle: Bundle?) {
        when (route) {
            NavigationRoute.LOGIN -> {
                val request = NavDeepLinkRequest.Builder.fromAction(route)
                mNavController?.navigate(request)
            }
        }
    }

    override fun back(): Boolean {
        return mNavController?.navigateUp() ?: false
    }

    override fun popBackStack(id: Int) {
        mNavController?.navigate(id)
    }

    override fun unbind() {
        mNavController = null
    }
}