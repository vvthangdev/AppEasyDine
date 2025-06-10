package com.module.features.login.ui

import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.module.core.ui.base.BaseFragment
import com.module.domain.api.model.UserRole
import com.module.features.login.R
import com.module.features.login.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    @Inject
    lateinit var mNavigation: LoginNavigation

    @Inject
    lateinit var nNavigation: LoginNavigator

    override val layoutId
        get() = R.layout.fragment_login

    private val mViewModel: LoginViewModel by viewModels()

    override fun getVM() = mViewModel

    private var isPasswordVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nNavigation.bind(findNavController()) // Sửa từ setNavController thành bind
    }

    override fun initView() {
        super.initView()
        setupSignUpLink()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val windowInsetsController = activity?.window?.insetsController
            windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            activity?.window?.statusBarColor =
                resources.getColor(com.module.core.ui.R.color.color_FF7622, null)
        } else {
            activity?.window?.statusBarColor =
                resources.getColor(com.module.core.ui.R.color.color_FF7622, null)
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        setupPasswordVisibilityToggle()

    }

    override fun observeViewModel() {
        super.observeViewModel()
        mViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginState.LoginSuccess -> {
                    when (state.role) {
                        UserRole.ADMIN -> mNavigation.openLoginToAdminHome()
                        UserRole.STAFF -> mNavigation.openLoginToAdminHome()
                        UserRole.CUSTOMER -> mNavigation.openLoginToUserHome()
                    }
                }

                is LoginState.LoginFailed -> {
                    Toast.makeText(
                        requireContext(),
                        state.e?.message ?: "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            mViewModel.login(email, password)
        }
    }

    private fun setupPasswordVisibilityToggle() {
        // Lưu trữ typeface ban đầu từ style đã được áp dụng
        val originalTypeface = binding.etPassword.typeface

        binding.ibPasswordVisibility.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ibPasswordVisibility.isSelected = true
            } else {
                binding.etPassword.inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ibPasswordVisibility.isSelected = false
            }

            // Khôi phục typeface sau khi thay đổi inputType
            binding.etPassword.typeface = originalTypeface
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }
    }

    private fun setupSignUpLink() {
        binding.clSignupPrompt.setOnClickListener {
            Timber.d("Navigating to SignUpFragment")
            nNavigation.openLoginToSignUp()
        }
    }
}