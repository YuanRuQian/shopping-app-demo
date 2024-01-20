package yuan.lydia.shoppingappdemo.data.history

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
import yuan.lydia.shoppingappdemo.network.history.HistoryRepository
import yuan.lydia.shoppingappdemo.network.history.Order
import yuan.lydia.shoppingappdemo.network.history.OrderHistoryResponse

sealed interface UiState {

    data class OrderHistorySuccess(val response: OrderHistoryResponse) : UiState

    data class OrderHistoryNetworkError(val message: String) : UiState

    data class OrderHistoryError(val response: OrderHistoryResponse) : UiState

    data object Uninitialized : UiState

    data object Loading : UiState
}

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState.Uninitialized)


    private val _orderHistory = MutableLiveData<List<Order>>()
    val orderHistory: LiveData<List<Order>>
        get() = _orderHistory

    private suspend fun getOrderHistoryHelper(token: String): UiState {
        return try {
            val response = historyRepository.getOrderHistory(token)
            if (response.status.success) {
                _orderHistory.value = response.orders
                UiState.OrderHistorySuccess(response)
            } else {
                UiState.OrderHistoryError(response)
            }
        } catch (e: retrofit2.HttpException) {
            UiState.OrderHistoryNetworkError("Network error, please try again.")
        }
    }

    fun getOrderHistory(token: String) {
        uiState = UiState.Loading
        viewModelScope.launch {
            uiState = getOrderHistoryHelper(token)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                HistoryViewModel(application.container.historyRepository)
            }
        }
    }
}