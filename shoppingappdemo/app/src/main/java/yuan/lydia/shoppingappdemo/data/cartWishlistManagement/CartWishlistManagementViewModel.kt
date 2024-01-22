package yuan.lydia.shoppingappdemo.data.cartWishlistManagement

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
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.CartItemEntity
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.WishlistItemEntity
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.repository.CartWishlistManagementRepository
import yuan.lydia.shoppingappdemo.network.cart.Order
import yuan.lydia.shoppingappdemo.network.cart.OrderRequest

class CartWishlistManagementViewModel(private val cartWishlistManagementRepository: CartWishlistManagementRepository) :
    ViewModel() {

    private val _userCartLiveData = MutableLiveData<List<CartItemEntity>>()
    val userCartLiveData: LiveData<List<CartItemEntity>> get() = _userCartLiveData

    private val _userWishlistLiveData = MutableLiveData<List<WishlistItemEntity>>()

    val userWishlistLiveData: LiveData<List<WishlistItemEntity>> get() = _userWishlistLiveData

    private val _checkoutSuccess = MutableLiveData<Boolean?>()
    val checkoutSuccess: LiveData<Boolean?> get() = _checkoutSuccess

    fun loadUserCartData(username: String) {
        viewModelScope.launch {
            cartWishlistManagementRepository.loadUserCartData(username).collect {
                _userCartLiveData.value = it
            }
        }
    }

    fun loadWishlistData(username: String) {
        viewModelScope.launch {
            cartWishlistManagementRepository.loadUserWishlistData(username).collect {
                _userWishlistLiveData.value = it
            }
        }
    }

    fun addToCart(username: String, productId: Long) {
        viewModelScope.launch {
            cartWishlistManagementRepository.increaseQuantity(username, productId, 1)
        }
    }

    fun addToWishlistAndReloadWishlistData(username: String, productId: Long) {
        viewModelScope.launch {
            cartWishlistManagementRepository.addToWishlist(username, productId)
            loadWishlistData(username)
        }
    }

    fun removeFromWishlistAndReloadWishlistData(username: String, productId: Long) {
        viewModelScope.launch {
            cartWishlistManagementRepository.removeFromWishlist(username, productId)
            loadWishlistData(username)
        }
    }

    fun updateQuantityThenReloadUserCartData(username: String, productId: Long, newQuantity: Int) {
        viewModelScope.launch {
            cartWishlistManagementRepository.updateQuantity(username, productId, newQuantity)
            loadUserCartData(username)
        }
    }

    fun increaseQuantityThenReloadUserCartData(username: String, productId: Long, addedQuantity: Int) {
        viewModelScope.launch {
            cartWishlistManagementRepository.increaseQuantity(username, productId, addedQuantity)
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
                val orderResponse = cartWishlistManagementRepository.submitOrder(token, orderRequest)
                if(orderResponse.success) {
                    _checkoutSuccess.value = true
                    Log.d("CartWishlistManagementViewModel", "checkout success")
                } else {
                    _checkoutSuccess.value = false
                    Log.d("CartWishlistManagementViewModel", "checkout failed: ${orderResponse.message}")
                }
            } catch (e: retrofit2.HttpException) {
                _checkoutSuccess.value = false
                Log.e("CartWishlistManagementViewModel", "checkout error: ${e.message()}")
            }
        }
    }

    fun clearUserCartAndReloadUserCartData(username: String) {
        viewModelScope.launch {
            cartWishlistManagementRepository.clearCart(username)
            loadUserCartData(username)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                CartWishlistManagementViewModel(application.container.cartWishlistManagementRepository)
            }
        }
    }

}