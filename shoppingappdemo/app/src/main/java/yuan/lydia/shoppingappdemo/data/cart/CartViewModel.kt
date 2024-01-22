package yuan.lydia.shoppingappdemo.data.cart

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
import yuan.lydia.shoppingappdemo.data.cart.database.CartItemEntity
import yuan.lydia.shoppingappdemo.data.cart.repository.CartRepository
import yuan.lydia.shoppingappdemo.network.cart.Order
import yuan.lydia.shoppingappdemo.network.cart.OrderRequest

class CartViewModel(private val cartRepository: CartRepository) :
    ViewModel() {

    private val _userCartLiveData = MutableLiveData<List<CartItemEntity>>()
    val userCartLiveData: LiveData<List<CartItemEntity>> get() = _userCartLiveData

    private val _checkoutSuccess = MutableLiveData<Boolean?>()
    val checkoutSuccess: LiveData<Boolean?> get() = _checkoutSuccess

    fun loadUserCartData(username: String) {
        viewModelScope.launch {
            cartRepository.loadUserCartData(username).collect {
                _userCartLiveData.value = it
            }
        }
    }

    fun addToCart(username: String, productId: Long) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(username, productId, 1)
        }
    }

    fun updateQuantityThenReloadUserCartData(username: String, productId: Long, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(username, productId, newQuantity)
            loadUserCartData(username)
        }
    }

    fun increaseQuantityThenReloadUserCartData(
        username: String,
        productId: Long,
        addedQuantity: Int
    ) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(username, productId, addedQuantity)
            loadUserCartData(username)
        }
    }

    private fun mapCartItemEntitiesToOrderRequest(cartItems: List<CartItemEntity>): OrderRequest {
        val orders = cartItems.map { cartItem ->
            Order(
                productId = cartItem.productId,
                quantity = cartItem.quantity.toLong()
            )
        }

        return OrderRequest(order = orders)
    }


    fun checkout(token: String, cartItems: List<CartItemEntity>) {
        val orderRequest = mapCartItemEntitiesToOrderRequest(cartItems)
        viewModelScope.launch {
            try {
                val orderResponse =
                    cartRepository.submitOrder(token, orderRequest)
                if (orderResponse.success) {
                    _checkoutSuccess.value = true
                    Log.d("CartWishlistManagementViewModel", "checkout success")
                } else {
                    _checkoutSuccess.value = false
                    Log.d(
                        "CartWishlistManagementViewModel",
                        "checkout failed: ${orderResponse.message}"
                    )
                }
            } catch (e: retrofit2.HttpException) {
                _checkoutSuccess.value = false
                Log.e("CartWishlistManagementViewModel", "checkout error: ${e.message()}")
            }
        }
    }

    fun clearUserCartAndReloadUserCartData(username: String) {
        viewModelScope.launch {
            cartRepository.clearCart(username)
            loadUserCartData(username)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                CartViewModel(application.container.cartRepository)
            }
        }
    }

}