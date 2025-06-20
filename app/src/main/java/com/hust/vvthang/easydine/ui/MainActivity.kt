package com.hust.vvthang.easydine.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.databinding.ActivityMainBinding
import com.hust.vvthang.easydine.navigation.AppNavigation
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseActivity
import com.module.core.utils.extensions.constants.PreferenceKey
import com.module.core.utils.extensions.shared_preferences.AppPreferences
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val layoutId: Int = R.layout.activity_main
    private val mViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var appPreferences: AppPreferences

    @Inject
    lateinit var appNavigation: AppNavigation

    @Inject
    lateinit var coreNavigation: CoreNavigation

    override fun getVM(): MainViewModel = mViewModel

    private var isSplashHandled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Đăng ký listener ở đây

        appPreferences.put(PreferenceKey.TABLE_ID, "")
        supportFragmentManager.setFragmentResultListener(
            "splash_result",
            this
        ) { _, bundle ->
            isSplashHandled = bundle.getBoolean("isSplashHandled", false)
            Timber.tag("MainActivity").d("vvt 08: Received isSplashHandled = $isSplashHandled")
        }
    }

    override fun preContentView() {
        super.preContentView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val splashScreen = installSplashScreen()
            Timber.tag("MainActivity").d("vvt 04: SplashScreen installed")
            splashScreen.setKeepOnScreenCondition {
                Timber.tag("MainActivity").d("vvt check: isSplashHandled = $isSplashHandled")
                !isSplashHandled
            }
        } else {
            setTheme(R.style.Theme_MyApp_Splash)
        }
    }

    override fun initView() {
        super.initView()
        Timber.tag("MainActivity").d("initView called")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        appNavigation.bind(navHostFragment.navController)
        coreNavigation.bind(navHostFragment.navController) // Thêm dòng này
        Timber.tag("MainActivity").d("Navigation bound")
    }

//    override fun onDestroy() {
//
//        super.onDestroy()
//
//    }
}