package yuan.lydia.shoppingappdemo.data.history

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
import yuan.lydia.shoppingappdemo.network.history.HistoryRepository
import yuan.lydia.shoppingappdemo.network.history.Order
import yuan.lydia.shoppingappdemo.network.history.OrderDetail

// TODO: how to sort the order history

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _orderHistory = MutableLiveData<List<Order>>()
    val orderHistory: LiveData<List<Order>>
        get() = _orderHistory

    private val _currentOrderDetails = MutableLiveData<OrderDetail?>(null)
    val currentOrderDetails: LiveData<OrderDetail?>
        get() = _currentOrderDetails

    fun clearCurrentOrderDetails() {
        _currentOrderDetails.value = null
    }

    private suspend fun getOrderHistoryHelper(token: String) {
        try {
            val response = historyRepository.getOrderHistory(token)
            if (response.status.success) {
                _orderHistory.value = response.orders
            } else {
                _orderHistory.value = emptyList()
                Log.e("HistoryViewModel", "get order history error: ${response.status.message}")
            }
        } catch (e: retrofit2.HttpException) {
            Log.e("HistoryViewModel", "get order history error: ${e.message()}")
            _orderHistory.value = emptyList()
        }
    }

    fun getOrderHistory(token: String) {
        viewModelScope.launch {
            getOrderHistoryHelper(token)
        }
    }

    private suspend fun getOrderDetailsHelper(token: String, orderId: String) {
        try {
            val response = historyRepository.getOrderDetails(token, orderId)
            if (response.status.success) {
                _currentOrderDetails.value = response.order
            } else {
                _currentOrderDetails.value = null
                Log.e("HistoryViewModel", "get order details error: ${response.status.message}")
            }
        } catch (e: retrofit2.HttpException) {
            Log.e("HistoryViewModel", "get order details error: ${e.message()}")
            _currentOrderDetails.value = null
        }
    }

    fun getOrderDetails(token: String, orderId: String) {
        viewModelScope.launch {
            getOrderDetailsHelper(token, orderId)
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