package com.hust.vvthang.easydine.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.databinding.FragmentAdHomeBinding
import com.hust.vvthang.easydine.navigation.AppNavigation
import com.module.admin.area.AreaFragment
import com.module.admin.order.OrderFragment
import com.module.admin.profile.ProfileFragment
import com.module.admin.sale.SalesFragment
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AdHomeFragment : BaseFragment<FragmentAdHomeBinding, AdHomeViewModel>() {
    override val layoutId: Int = R.layout.fragment_ad_home
    private val viewModel: AdHomeViewModel by viewModels()
    override fun getVM(): AdHomeViewModel = viewModel

    @Inject
    lateinit var coreNavigation: CoreNavigation

    @Inject
    lateinit var appNavigation: AppNavigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing AdHomeFragment")
        setupViewPagerAndBottomNavigation()
        coreNavigation.bind(findNavController())
        appNavigation.bind(findNavController())
    }

    private fun setupViewPagerAndBottomNavigation() {
        val viewPager = binding.homeViewPager
        val bottomNavigation = binding.bottomNavigation

        val adapter = HomePagerAdapter(this)
        viewPager.adapter = adapter

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.salesFragment -> viewPager.currentItem = 0
                R.id.areaFragment -> viewPager.currentItem = 1
                R.id.orderFragment -> viewPager.currentItem = 2
                R.id.profileFragment -> viewPager.currentItem = 3
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

    fun gotoCart(bundle: Bundle) {
        findNavController().navigate(R.id.action_adHomeFragment_to_cartFragment,bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coreNavigation.unbind()
    }

    private class HomePagerAdapter(private val fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> SalesFragment()
                1 -> AreaFragment().apply {
                    viewPager = (fragment as AdHomeFragment).binding.homeViewPager
                }
//                1 -> AreaFragment().apply {
//                    tableClickListener = object : OnTableClickListener {
//                        override fun onTableClicked() {
//                            // Chuyển đến trang SalesFragment (trang 0)
//                            (fragment as AdHomeFragment).binding.homeViewPager.currentItem = 0
//                        }
//                    }
//                }
                2 -> OrderFragment()
                3 -> ProfileFragment()
                else -> SalesFragment()
            }
        }
    }
}