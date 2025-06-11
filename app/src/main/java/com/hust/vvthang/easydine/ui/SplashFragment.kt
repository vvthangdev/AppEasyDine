package com.hust.vvthang.easydine.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.databinding.FragmentSplashBinding
import com.module.core.utils.extensions.constants.PreferenceKey
import com.module.core.utils.extensions.shared_preferences.AppPreferences
import com.module.domain.api.model.UserRole
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)
        Timber.tag("SplashFragment").d("onViewCreated called")

        binding.progressBar.visibility = View.VISIBLE
        Timber.tag("SplashFragment").d("vvt 05: ProgressBar shown")

        CoroutineScope(Dispatchers.Main).launch {
            Timber.tag("SplashFragment").d("Starting 1000ms delay")
            delay(1000) // Tăng delay để MainActivity có thời gian đăng ký listener
            Timber.tag("SplashFragment").d("Delay finished, calling handleUserRoleAndNavigation")
            handleUserRoleAndNavigation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Timber.tag("SplashFragment").d("onDestroyView called")
    }

    private fun handleUserRoleAndNavigation() {
        Timber.tag("SplashFragment").d("handleUserRoleAndNavigation started")
        val refreshToken = appPreferences.get(PreferenceKey.REFRESH_TOKEN, "")
        Timber.tag("SplashFragment").d("Refresh token: ${if (refreshToken.isNotEmpty()) "exists" else "empty"}")

        val result = Bundle().apply {
            putBoolean("isSplashHandled", true)
        }
        // Sử dụng activity.supportFragmentManager thay vì parentFragmentManager
        activity?.supportFragmentManager?.setFragmentResult("splash_result", result)
        Timber.tag("SplashFragment").d("vvt 07: Sent isSplashHandled = true to MainActivity")

        if (refreshToken.isNotEmpty()) {
            val role = UserRole.fromString(appPreferences.get(PreferenceKey.USER_ROLE, ""))
            Timber.tag("SplashFragment").d("User role: $role")
            when (role) {
                UserRole.ADMIN, UserRole.STAFF -> {
                    Timber.tag("SplashFragment").d("vvt 01: Navigating to AdHomeFragment")
                    findNavController().navigate(R.id.action_splashFragment_to_adHomeFragment)
                }
                UserRole.CUSTOMER -> {
                    Timber.tag("SplashFragment").d("vvt 02: Navigating to UserHomeFragment")
                    findNavController().navigate(R.id.action_splashFragment_to_userHomeFragment)
                }
                else -> {
                    Timber.tag("SplashFragment").d("vvt 03: Navigating to LoginFragment, invalid role")
                    findNavController().navigate(R.id.action_to_loginNav)
                }
            }
        } else {
            findNavController().navigate(R.id.action_to_loginNav)
        }

        binding.progressBar.visibility = View.GONE
        Timber.tag("SplashFragment").d("vvt 06: ProgressBar hidden")

    }
}