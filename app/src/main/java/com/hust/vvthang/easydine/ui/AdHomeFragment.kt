package com.hust.vvthang.easydine.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.hust.vvthang.easydine.R
import com.hust.vvthang.easydine.databinding.FragmentAdHomeBinding
import com.hust.vvthang.easydine.navigation.AdHomeNavigation
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
    lateinit var adHomeNavigation: AdHomeNavigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing AdHomeFragment")
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.home_nav_graph) as NavHostFragment
        val navController = navHostFragment.navController
        Timber.d("Binding NavController: $navController")
        adHomeNavigation.bind(navController)
        binding.bottomNavigation.setupWithNavController(navController)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            Timber.d("BottomNavigation selected item: ${item.itemId}")
            viewModel.setSelectedTab(item.itemId)
            try {
                navController.navigate(item.itemId)
                true
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Failed to navigate to item: ${item.itemId}")
                Toast.makeText(requireContext(), "Navigation failed, please try again", Toast.LENGTH_SHORT).show()
                false
            }
        }

        // Lắng nghe thay đổi destination để đồng bộ BottomNavigationView
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Timber.d("Destination changed to: ${destination.id}")
            viewModel.setSelectedTab(destination.id)
        }

        viewModel.selectedTab.observe(viewLifecycleOwner) { tabId ->
            if (binding.bottomNavigation.selectedItemId != tabId) {
                Timber.d("Setting BottomNavigation to tabId: $tabId")
                binding.bottomNavigation.selectedItemId = tabId
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adHomeNavigation.unbind()
    }
}