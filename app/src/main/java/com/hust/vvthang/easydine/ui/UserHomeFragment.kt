package com.hust.vvthang.easydine.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.databinding.FragmentUserHomeBinding
import com.hust.vvthang.easydine.navigation.AppNavigation
import com.module.admin.profile.ProfileFragment
import com.module.admin.sale.SalesFragment
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import com.module.features.cameraqr.CameraQrFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class UserHomeFragment : BaseFragment<FragmentUserHomeBinding, UserHomeViewModel>() {
    override val layoutId: Int = R.layout.fragment_user_home
    private val viewModel: UserHomeViewModel by viewModels()
    override fun getVM(): UserHomeViewModel = viewModel

    @Inject
    lateinit var coreNavigation: CoreNavigation

    @Inject
    lateinit var appNavigation: AppNavigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing UserHomeFragment")
        setupViewPagerAndBottomNavigation()
        coreNavigation.bind(findNavController())
        // appNavigation.bind(findNavController())
    }

    private fun setupViewPagerAndBottomNavigation() {
        val viewPager = binding.userHomeViewPager
        val bottomNavigation = binding.bottomNavigation

        val adapter = UserHomePagerAdapter(this)
        viewPager.adapter = adapter

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.salesFragment -> viewPager.currentItem = 0
                R.id.cameraQRFragment -> viewPager.currentItem = 1
                R.id.profileFragment -> viewPager.currentItem = 2
            }
            true
        }

        // Đồng bộ ViewPager2 với BottomNavigationView
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigation.menu.getItem(position).isChecked = true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coreNavigation.unbind()
        appNavigation.unbind()
    }

    private class UserHomePagerAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SalesFragment()
                1 -> CameraQrFragment().apply {
                    viewPager = (fragment as UserHomeFragment).binding.userHomeViewPager
                }
                2 -> ProfileFragment()
                else -> SalesFragment()
            }
        }
    }
}