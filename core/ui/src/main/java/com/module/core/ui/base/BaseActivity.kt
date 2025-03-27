package com.module.core.ui.base

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import java.lang.ref.WeakReference

abstract class BaseActivity<BD : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    private var _binding: BD? = null

    protected val binding: BD?
        get() {
            return _binding
        }

    private lateinit var viewModel: VM

    @get: LayoutRes
    abstract val layoutId: Int
    abstract fun getVM(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        preContentView()
        _binding = DataBindingUtil.setContentView(WeakReference(this).get()!!, layoutId)
        _binding?.lifecycleOwner = this
        viewModel = getVM()

        initView()

        setOnClick()

        bindingStateView()
    }

    open fun preContentView() {

    }


    open fun initView() {

    }

    open fun setOnClick() {

    }

    open fun bindingStateView() {

    }

    override fun onDestroy() {
        _binding?.unbind()
        _binding = null
        super.onDestroy()
    }
}