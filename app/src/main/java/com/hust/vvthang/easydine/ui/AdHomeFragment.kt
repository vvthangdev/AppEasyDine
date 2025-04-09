package com.hust.vvthang.easydine.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hust.vvthang.easydine.AdHomeViewModel
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.databinding.FragmentAdHomeBinding
import com.module.core.ui.base.BaseFragment

class AdHomeFragment : BaseFragment<FragmentAdHomeBinding, AdHomeViewModel>() {
    override val layoutId: Int = R.layout.fragment_ad_home
    private val viewModel: AdHomeViewModel by viewModels()
    override fun getVM(): AdHomeViewModel = viewModel

    override fun initView() {
//        super.initView()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.home_nav_graph) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
    }
}