package com.module.admin.profile

import android.os.Bundle
import com.module.core.navigation.BaseNavigator

interface ProfileNavigator: BaseNavigator {
    fun openProfileToEditProfile(bundle: Bundle? = null)
}