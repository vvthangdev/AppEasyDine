package com.module.admin.area

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.module.core.ui.base.BaseFragment
import com.module.admin.area.databinding.FragmentAreaBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class AreaFragment : BaseFragment<FragmentAreaBinding, AreaViewModel>() {
    override val layoutId: Int
        get() = R.layout.fragment_area

    private val mViewModel: AreaViewModel by viewModels()
    override fun getVM(): AreaViewModel = mViewModel

    private lateinit var tableAdapter: TableAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing AreaFragment view")
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Setup RecyclerView
        tableAdapter = TableAdapter()
        binding.recyclerViewTables.apply {
            layoutManager = GridLayoutManager(context, 3) // 3 columns grid
            adapter = tableAdapter
        }
    }

    override fun observeViewModel() {
        mViewModel.tableStatuses.observe(viewLifecycleOwner) { tableStatuses ->
            Timber.d("Updating table statuses in UI: ${tableStatuses.size} tables")
            tableAdapter.submitList(tableStatuses)
        }
    }
}