package com.module.features.login.ui

import android.os.Bundle
import com.module.core.navigation.BaseNavigator
import com.module.core.navigation.BaseNavigatorImpl
import com.module.features.login.R
import javax.inject.Inject

interface LoginNavigator: BaseNavigator {
    fun openLoginToSignUp(bundle: Bundle? = null)
}
class LoginNavigatorImpl @Inject constructor() :  BaseNavigatorImpl(), LoginNavigator {
    override fun openLoginToSignUp(bundle: Bundle?) {
        goTo(R.id.action_loginFragment_to_signUpFragment, bundle)
    }
}