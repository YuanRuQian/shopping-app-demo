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
import yuan.lydia.shoppingappdemo.network.extractErrorBody

class CartViewModel(private val cartRepository: CartRepository) :
    ViewModel() {

    private val _userCartLiveData = MutableLiveData<List<CartItemEntity>>()
    val userCartLiveData: LiveData<List<CartItemEntity>> get() = _userCartLiveData

    fun loadUserCartData(username: String) {
        viewModelScope.launch {
            cartRepository.loadUserCartData(username).collect {
                _userCartLiveData.value = it
            }
        }
    }

    fun updateQuantityThenReloadUserCartData(username: String, productId: Long, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(username, productId, newQuantity)
            loadUserCartData(username)
        }
    }

    fun increaseQuantity(username: String, productId: Long, addedQuantity: Int) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(username, productId, addedQuantity)
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


    fun checkout(
        token: String,
        username: String,
        cartItems: List<CartItemEntity>,
        onCheckoutSuccess: (String) -> Unit,
        onCheckoutFailure: (String) -> Unit
    ) {
        val orderRequest = mapCartItemEntitiesToOrderRequest(cartItems)
        viewModelScope.launch {
            try {
                val orderResponse =
                    cartRepository.submitOrder(token, orderRequest)
                if (orderResponse.success) {
                    onCheckoutSuccess(username)
                    Log.d("CartWishlistManagementViewModel", "checkout success")
                } else {
                    Log.d(
                        "CartWishlistManagementViewModel",
                        "checkout failed: ${orderResponse.message}"
                    )
                    onCheckoutFailure(orderResponse.message)
                }
            } catch (e: retrofit2.HttpException) {
                Log.e("CartWishlistManagementViewModel", "checkout error: ${e.message()}")
                onCheckoutFailure(extractErrorBody(e))
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