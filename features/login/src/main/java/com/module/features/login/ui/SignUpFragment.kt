package com.module.features.login.ui

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.module.core.ui.base.BaseFragment
import com.module.features.login.R
import com.module.features.login.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {

    @Inject
    lateinit var mNavigation: LoginNavigation

    override val layoutId: Int
        get() = R.layout.fragment_sign_up

    private val mViewModel: SignUpViewModel by viewModels()

    override fun getVM(): SignUpViewModel {
        return mViewModel
    }

    override fun initView() {
        super.initView()
        setupSignUpButton()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        mViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SignUpState.SignUpSuccess -> {
                    // Hiển thị dialog thành công
                    val dialog = SignUpSuccessDialogFragment.newInstance(state.username)
                    dialog.show(parentFragmentManager, "SignUpSuccessDialog")
                }
                is SignUpState.SignUpFailed -> {
                    Toast.makeText(
                        requireContext(),
                        state.e?.message ?: "Sign up failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupSignUpButton() {
        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val name = binding.etName.text.toString()
            val username = binding.etUsername.text.toString()
            val phone = binding.etPhone.text.toString()
            val password = binding.etPassword.text.toString()

            mViewModel.signUp(email, name, username, phone, password)
        }
    }
}