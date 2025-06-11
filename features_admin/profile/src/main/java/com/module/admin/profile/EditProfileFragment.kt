package com.module.admin.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.module.admin.profile.databinding.FragmentEditProfileBinding
import com.module.core.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding, EditProfileViewModel>() {

    override val layoutId: Int = R.layout.fragment_edit_profile

    private val mViewModel: EditProfileViewModel by viewModels()
    override fun getVM(): EditProfileViewModel = mViewModel

    override fun initView() {
        super.initView()
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString()
            val phone = binding.etPhone.text.toString()
            val address = binding.etAddress.text.toString()
            val avatar = binding.etAvatar.text.toString()
            mViewModel.updateUser(name, phone, address, avatar)
        }
    }

    override fun observeViewModel() {
        mViewModel.userInfo.observe(viewLifecycleOwner) { user ->
            user?.let {
                with(binding) {
                    etName.setText(it.name)
                    etPhone.setText(it.phone)
                    etAddress.setText(it.address)
                    etAvatar.setText(it.avatar)
                    tvUsername.text = it.username
                    tvEmail.text = it.email
                    tvRole.text = it.role
                    tvCreatedAt.text = it.createdAt
                    // Tải avatar bằng Glide
                    Glide.with(this@EditProfileFragment)
                        .load(it.avatar)
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

        mViewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }
}