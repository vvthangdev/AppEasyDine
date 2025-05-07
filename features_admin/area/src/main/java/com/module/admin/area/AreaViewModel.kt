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

    init {
        loadTableStatuses()
    }

    private fun loadTableStatuses() {
        viewModelScope.launch {
            tableRepository.getAllTableStatuses().collect { result ->
                when (result) {
                    is Result.Loading -> Timber.d("Loading table statuses...")
                    is Result.Success -> {
                        _tableStatuses.postValue(result.data ?: emptyList())
                        Timber.d("Loaded ${result.data?.size} table statuses")
                    }
                    is Result.Error -> Timber.e(result.exception, "Error loading table statuses: ${result.message}")
                }
            }
        }
    }
}