package com.hust.vvthang.easydine.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.databinding.FragmentAdHomeBinding
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UserHomeFragment : BaseFragment<FragmentAdHomeBinding, UserHomeViewModel>() {
    override val layoutId: Int = R.layout.fragment_ad_home
    private val viewModel: UserHomeViewModel by viewModels()
    override fun getVM(): UserHomeViewModel = viewModel

    @Inject
    lateinit var coreNavigation: CoreNavigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing AdHomeFragment")
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.home_nav_graph) as NavHostFragment
        val navController = navHostFragment.navController
        Timber.d("Binding NavController: $navController")
        coreNavigation.bind(navController)
        binding.bottomNavigation.setupWithNavController(navController)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coreNavigation.unbind()
    }
}