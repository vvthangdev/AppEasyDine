package com.module.admin.sale

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.core.network.model.Result
import com.module.core.ui.base.BaseViewModel
import com.module.domain.api.model.Category
import com.module.domain.api.model.Item
import com.module.domain.api.repository.ItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class SalesState {
    data class ItemsLoaded(val items: List<Item>) : SalesState()
    data class CategoriesLoaded(val categories: List<Category>) : SalesState()
    data class Error(val exception: Throwable?) : SalesState()
    object Loading : SalesState()
}

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val itemRepository: ItemRepository
) : BaseViewModel() {

    val uiState = MutableLiveData<SalesState>()

    init {
        Timber.d("Initializing SalesViewModel")
        loadCategories()
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            itemRepository.getAllItems().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        uiState.postValue(SalesState.Loading)
                        isLoading.postValue(true)
                    }
                    is Result.Success -> {
                        isLoading.postValue(false)
                        result.data?.let { items ->
                            uiState.postValue(SalesState.ItemsLoaded(items))
                        } ?: uiState.postValue(SalesState.Error(Exception("No items found")))
                    }
                    is Result.Error -> {
                        isLoading.postValue(false)
                        uiState.postValue(SalesState.Error(result.exception))
                    }
                }
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            itemRepository.getAllCategories().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        uiState.postValue(SalesState.Loading)
                        isLoading.postValue(true)
                    }
                    is Result.Success -> {
                        isLoading.postValue(false)
                        result.data?.let { categories ->
                            uiState.postValue(SalesState.CategoriesLoaded(categories))
                        } ?: uiState.postValue(SalesState.Error(Exception("No categories found")))
                    }
                    is Result.Error -> {
                        isLoading.postValue(false)
                        uiState.postValue(SalesState.Error(result.exception))
                    }
                }
            }
        }
    }

    fun searchItems(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            if (query.isBlank()) {
                loadItems() // Load all items if query is empty
            } else {
                itemRepository.searchItem(query).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            uiState.postValue(SalesState.Loading)
                            isLoading.postValue(true)
                        }
                        is Result.Success -> {
                            isLoading.postValue(false)
                            result.data?.let { items ->
                                uiState.postValue(SalesState.ItemsLoaded(items))
                            } ?: uiState.postValue(SalesState.Error(Exception("No items found")))
                        }
                        is Result.Error -> {
                            isLoading.postValue(false)
                            uiState.postValue(SalesState.Error(result.exception))
                        }
                    }
                }
            }
        }
    }

    fun filterItemsByCategory(categoryId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            if (categoryId.isBlank()) {
                loadItems() // Load all items if no category is selected
            } else {
                itemRepository.filterItemsByCategory(categoryId).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            uiState.postValue(SalesState.Loading)
                            isLoading.postValue(true)
                        }
                        is Result.Success -> {
                            isLoading.postValue(false)
                            result.data?.let { items ->
                                uiState.postValue(SalesState.ItemsLoaded(items))
                            } ?: uiState.postValue(SalesState.Error(Exception("No items found for category")))
                        }
                        is Result.Error -> {
                            isLoading.postValue(false)
                            uiState.postValue(SalesState.Error(result.exception))
                        }
                    }
                }
            }
        }
    }
}