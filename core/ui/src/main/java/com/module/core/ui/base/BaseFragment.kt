package com.module.core.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<BD : ViewDataBinding, VM : BaseViewModel> : Fragment() {

    private var _binding: BD? = null

    private lateinit var mViewModel: VM

    protected val binding: BD
        get() = _binding
            ?: throw IllegalStateException("Binding is only valid between onCreateView and onDestroyView")

    @get:LayoutRes
    abstract val layoutId: Int

    abstract fun getVM(): VM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = getVM()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        _binding?.let { return it.root }
            ?: throw IllegalArgumentException("Binding variable is null")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.let {
            it.lifecycleOwner = viewLifecycleOwner

            initView()

            setOnClick()

            bindingStateView()

            observeViewModel()
        }
    }

    open fun observeViewModel() {

    }

    open fun initView() {

    }

    open fun setOnClick() {

    }

    open fun bindingStateView() {

    }

    override fun onDestroyView() {
        _binding?.unbind()
        _binding = null
        super.onDestroyView()
    }
}