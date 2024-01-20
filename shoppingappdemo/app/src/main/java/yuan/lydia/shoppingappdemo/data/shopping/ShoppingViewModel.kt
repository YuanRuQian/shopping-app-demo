package yuan.lydia.shoppingappdemo.data.shopping

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import yuan.lydia.shoppingappdemo.ShoppingApplication
import yuan.lydia.shoppingappdemo.network.shopping.Product
import yuan.lydia.shoppingappdemo.network.shopping.ProductsResponse
import yuan.lydia.shoppingappdemo.network.shopping.ShoppingRepository

sealed interface UiState {

    data class ProductsSuccess(val response: ProductsResponse) : UiState

    data class ProductNetworkError(val message: String) : UiState

    data class ProductError(val response: ProductsResponse) : UiState

    data object Uninitialized : UiState

    data object Loading : UiState
}

class ShoppingViewModel(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState.Uninitialized)


    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>>
        get() = _products

    private suspend fun getProductsHelper(token: String): UiState {
        return try {
            val response = shoppingRepository.getProducts(token)
            if (response.status.success) {
                _products.value = response.products
                UiState.ProductsSuccess(response)
            } else {
                UiState.ProductError(response)
            }
        } catch (e: retrofit2.HttpException) {
            UiState.ProductNetworkError("Network error, please try again.")
        }
    }

    fun getProducts(token: String) {
        uiState = UiState.Loading
        viewModelScope.launch {
            uiState = getProductsHelper(token)
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