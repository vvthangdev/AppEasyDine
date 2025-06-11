package com.module.admin.order

import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.module.admin.order.databinding.FragmentOrderBinding
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import timber.log.Timber
import javax.inject.Inject

class OrderFragment : BaseFragment<FragmentOrderBinding, OrderViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_order

    private val mViewModel: OrderViewModel by viewModels()
    override fun getVM(): OrderViewModel {
        return mViewModel
    }

    @Inject
    lateinit var mCoreNavigation: CoreNavigation

    fun setNavController(navController: NavController) {
        mCoreNavigation.bind(navController)
        Timber.d("SalesFragment: NavController bound")
    }

}