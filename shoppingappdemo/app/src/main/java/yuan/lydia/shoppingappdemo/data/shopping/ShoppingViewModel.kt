package yuan.lydia.shoppingappdemo.data.shopping

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import yuan.lydia.shoppingappdemo.ShoppingApplication
import yuan.lydia.shoppingappdemo.network.common.Product
import yuan.lydia.shoppingappdemo.network.shopping.ShoppingRepository
import yuan.lydia.shoppingappdemo.ui.screens.shopping.FilterType


class ShoppingViewModel(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    private val _filteredProducts = MutableLiveData<List<Product>>()
    val filteredProducts: LiveData<List<Product>>
        get() = _filteredProducts

    private suspend fun getProductsHelper(token: String) {
        try {
            val response = shoppingRepository.getProducts(token)
            if (response.status.success) {
                _products.value = response.products
            } else {
                _products.value = emptyList()
                Log.e("ShoppingViewModel", "get products error: ${response.status.message}")
            }
        } catch (e: retrofit2.HttpException) {
            _products.value = emptyList()
            Log.e("ShoppingViewModel", "get products error: ${e.message()}")
        }
    }

    fun getProducts(token: String) {
        viewModelScope.launch {
            getProductsHelper(token)
            applyFilter(FilterType.NONE, null)
        }
    }

    fun getFilteredProducts(filterType: FilterType, maxPrice: Int?) {
        applyFilter(filterType, maxPrice)
    }

    private fun applyFilter(filterType: FilterType, maxPrice: Int?) {
        _filteredProducts.value = when (filterType) {
            FilterType.NONE -> _products.value ?: emptyList()
            FilterType.LOW_TO_HIGH -> _products.value?.sortedBy { it.retailPrice } ?: emptyList()
            FilterType.HIGH_TO_LOW -> _products.value?.sortedByDescending { it.retailPrice }
                ?: emptyList()

            FilterType.ALPHABETICAL_ASC -> _products.value?.sortedBy { it.name } ?: emptyList()
            FilterType.ALPHABETICAL_DESC -> _products.value?.sortedByDescending { it.name }
                ?: emptyList()

            FilterType.NO_MORE_THAN_MAX_PRICE -> if (maxPrice != null) {
                _products.value?.filter { it.retailPrice <= maxPrice } ?: emptyList()
            } else {
                _products.value ?: emptyList()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                ShoppingViewModel(application.container.shoppingRepository)
            }
        }
    }
}
