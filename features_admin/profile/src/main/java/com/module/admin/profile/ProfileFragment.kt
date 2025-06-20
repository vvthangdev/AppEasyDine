package com.module.admin.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.module.admin.profile.databinding.FragmentProfileBinding
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {

    override val layoutId: Int = R.layout.fragment_profile

    private val mViewModel: ProfileViewModel by viewModels()
    override fun getVM(): ProfileViewModel = mViewModel

    @Inject
    lateinit var mCoreNavigation: CoreNavigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Binding NavController for ProfileFragment")
    }

    override fun initView() {
        super.initView()
        binding.btnEditProfile.setOnClickListener {
            Timber.d("Navigating to EditProfileFragment")
            try {
                mCoreNavigation.openProfileToEditProfile()
            } catch (e: IllegalArgumentException) {
                Timber.e(e, "Navigation error")
                Toast.makeText(requireContext(), "Không thể điều hướng đến màn hình chỉnh sửa", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            Timber.d("Logout button clicked")
            mViewModel.logout()
        }
    }

    override fun observeViewModel() {
        mViewModel.userInfo.observe(viewLifecycleOwner) { user ->
            user?.let {
                with(binding) {
                    tvUsername.text = it.username
                    tvName.text = it.name
                    tvEmail.text = it.email
                    tvPhone.text = it.phone
                    tvRole.text = it.role
                    tvAddress.text = it.address
                    tvCreatedAt.text = it.createdAt
                    Glide.with(this@ProfileFragment)
                        .load(it.avatar.takeIf { !it.isNullOrBlank() } ?: com.module.core.ui.R.drawable.avatar_placeholder)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .circleCrop()
                        .into(ivAvatar)
                }
            }
        }

        mViewModel.profileIsLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        mViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                mViewModel.clearErrorMessage()
            }
        }

        mViewModel.logoutSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Timber.d("Logout successful, navigating to login screen")
                try {
//                    mCoreNavigation.openSplashFragment()
                    requireActivity().finish()
                } catch (e: IllegalArgumentException) {
                    Timber.e(e, "Navigation error to login screen")
                    Toast.makeText(requireContext(), "Không thể điều hướng đến màn hình đăng nhập", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
//        mCoreNavigation.unbind()
        super.onDestroyView()
    }
}