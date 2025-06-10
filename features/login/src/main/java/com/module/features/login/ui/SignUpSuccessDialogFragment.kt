package com.module.features.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.module.features.login.R
import com.module.features.login.databinding.FragmentSignUpSuccessDialogBinding

class SignUpSuccessDialogFragment : DialogFragment() {

    private var _binding: FragmentSignUpSuccessDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpSuccessDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy username từ arguments
        val username = arguments?.getString(ARG_USERNAME) ?: ""
        binding.tvSuccessMessage.text = getString(R.string.signup_success_message, username)

        // Xử lý nút OK
        binding.btnOk.setOnClickListener {
            dismiss()
            // Quay về LoginFragment
            parentFragmentManager.popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        // Tùy chỉnh kích thước dialog (ví dụ: 80% chiều rộng màn hình)
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_USERNAME = "username"

        fun newInstance(username: String): SignUpSuccessDialogFragment {
            return SignUpSuccessDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
        }
    }
}