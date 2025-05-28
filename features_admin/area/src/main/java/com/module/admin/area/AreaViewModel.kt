package com.module.admin.area

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.TableStatus
import com.module.domain.api.repository.TableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AreaViewModel @Inject constructor(
    private val tableRepository: TableRepository
) : BaseViewModel() {

    private val _tableStatuses = MutableLiveData<List<TableStatus>>()
    val tableStatuses: LiveData<List<TableStatus>> = _tableStatuses

    private val _areas = MutableLiveData<List<String>>()
    val areas: LiveData<List<String>> = _areas

    private val _selectedArea = MutableLiveData<String>()
    val selectedArea: LiveData<String> = _selectedArea

    private var allTableStatuses: List<TableStatus> = emptyList()

    init {
        loadTableStatuses()
    }

    fun loadTableStatuses() {
        viewModelScope.launch {
            tableRepository.getAllTableStatuses().collect { result ->
                when (result) {
                    is Result.Loading -> Timber.d("Loading table statuses...")
                    is Result.Success -> {
                        allTableStatuses = result.data ?: emptyList()
                        Timber.d("Loaded ${allTableStatuses.size} table statuses")

                        // Extract unique areas
                        val uniqueAreas = allTableStatuses.map { it.area }.distinct()
                        _areas.postValue(uniqueAreas)

                        // Set default selected area (first area or empty)
                        if (uniqueAreas.isNotEmpty()) {
                            _selectedArea.postValue(uniqueAreas[0])
                            filterTablesByArea(uniqueAreas[0])
                        } else {
                            _tableStatuses.postValue(emptyList())
                        }
                    }
                    is Result.Error -> Timber.e(result.exception, "Error loading table statuses: ${result.message}")
                }
            }
        }
    }

    fun selectArea(area: String) {
        _selectedArea.postValue(area)
        filterTablesByArea(area)
    }

    private fun filterTablesByArea(area: String) {
        val filteredTables = allTableStatuses.filter { it.area == area }
        _tableStatuses.postValue(filteredTables)
        Timber.d("Filtered ${filteredTables.size} tables for area: $area")
    }
}