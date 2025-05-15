package com.module.admin.area

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.module.admin.area.databinding.FragmentAreaBinding
import com.module.core.navigation.AdHomeNavigation
import com.module.core.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AreaFragment : BaseFragment<FragmentAreaBinding, AreaViewModel>() {
    override val layoutId: Int = R.layout.fragment_area

    private val mViewModel: AreaViewModel by viewModels()
    override fun getVM(): AreaViewModel = mViewModel

//    @Inject
//    lateinit var mNavigator: AreaNavigation

    @Inject
    lateinit var mAdHomeNavigation: AdHomeNavigation

    private lateinit var tableAdapter: TableAdapter
    private lateinit var areaAdapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("Initializing AreaFragment view")
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        areaAdapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf()
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = getItem(position)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView.text = getItem(position)
                return view
            }
        }
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerArea.adapter = areaAdapter
        binding.spinnerArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val area = areaAdapter.getItem(position)
                area?.let { mViewModel.selectArea(it) }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        tableAdapter = TableAdapter { tableStatus ->
            val bundle = Bundle().apply {
                putString("tableId", tableStatus.tableId)
                putInt("tableNumber", tableStatus.tableNumber)
            }
            Timber.d("Navigating to SalesFragment with tableId: ${tableStatus.tableId}")
            mAdHomeNavigation.openAreaToSales(bundle)
        }
        binding.recyclerViewTables.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = tableAdapter
        }
    }

    override fun observeViewModel() {
        mViewModel.areas.observe(viewLifecycleOwner) { areas ->
            areaAdapter.clear()
            areaAdapter.addAll(areas)
            areaAdapter.notifyDataSetChanged()
        }

        mViewModel.selectedArea.observe(viewLifecycleOwner) { selectedArea ->
            Timber.d("Selected area: $selectedArea")
            val index = areaAdapter.getPosition(selectedArea)
            if (index >= 0 && binding.spinnerArea.selectedItemId != index.toLong()) {
                binding.spinnerArea.setSelection(index)
            }
        }

        mViewModel.tableStatuses.observe(viewLifecycleOwner) { tableStatuses ->
            Timber.d("Updating table statuses in UI: ${tableStatuses.size} tables")
            tableAdapter.submitList(tableStatuses)
        }
    }
}