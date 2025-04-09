package com.hust.vvthang.easydine.ui

import android.os.Build
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.databinding.ActivityMainBinding
import com.hust.vvthang.easydine.navigation.AppNavigation
import com.module.core.ui.base.BaseActivity
import com.module.core.utils.extensions.constants.PreferenceKey
import com.module.core.utils.extensions.shared_preferences.AppPreferences
import com.module.features.login.ui.LoginNavigation
import com.module.features.login.ui.UserRole
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val layoutId: Int = R.layout.activity_main

    private val mViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var loginNavigation: LoginNavigation

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun getVM(): MainViewModel = mViewModel

    override fun initView() {
        super.initView()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        appNavigation.bind(navHostFragment.navController)
    }

    override fun preContentView() {
        super.preContentView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Timber.tag("MainActivity").d("SplashScreen API")
            // Use SplashScreen API
            val splashScreen = installSplashScreen()
            splashScreen.setKeepOnScreenCondition {
                val refreshToken = appPreferences.get(PreferenceKey.REFRESH_TOKEN, "")
                if (refreshToken.isNotEmpty()) {
                    val role = UserRole.fromString(appPreferences.get(PreferenceKey.USER_ROLE, ""))
                    when (role) {
                        UserRole.ADMIN, UserRole.STAFF -> loginNavigation.openLoginToAdminHome()
                        UserRole.CUSTOMER -> loginNavigation.openLoginToUserHome()
                        else -> {}
                    }
                }
                false
            }
        } else {
            // Fallback to theme-based splash for older versions
            setTheme(R.style.AppTheme)
        }
    }

    override fun setOnClick() {
        super.setOnClick()
    }

    override fun bindingStateView() {
        super.bindingStateView()
    }
}