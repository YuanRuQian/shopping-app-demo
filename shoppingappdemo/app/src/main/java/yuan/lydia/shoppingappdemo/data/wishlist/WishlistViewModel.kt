package yuan.lydia.shoppingappdemo.data.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import yuan.lydia.shoppingappdemo.ShoppingApplication
import yuan.lydia.shoppingappdemo.network.wishlist.Product
import yuan.lydia.shoppingappdemo.network.wishlist.WishlistRepository


class WishlistViewModel(private val wishlistRepository: WishlistRepository) : ViewModel() {

    private val _wishlist = MutableLiveData<List<Product>>()
    val wishlist: LiveData<List<Product>>
        get() = _wishlist

    private val _addToWishlistSuccess = MutableLiveData<Boolean?>()

    val addToWishlistSuccess: LiveData<Boolean?>
        get() = _addToWishlistSuccess

    private val _removeFromWishlistSuccess = MutableLiveData<Boolean?>()

    val removeFromWishlistSuccess: LiveData<Boolean?>
        get() = _removeFromWishlistSuccess

    fun loadWishlist(token: String) {
        viewModelScope.launch {
            _wishlist.value = wishlistRepository.getWishlist(token).products
        }
    }

    // TODO: handle request error
    fun addToWishlistAndReloadWishlistData(token: String, productId: Long) {
        viewModelScope.launch {
            wishlistRepository.addProductToWishlist(token, productId)
            loadWishlist(token)
        }
    }

    // TODO: handle request error
    fun removeFromWishlistAndReloadWishlistData(token: String, productId: Long) {
        viewModelScope.launch {
            wishlistRepository.removeProductFromWishlist(token, productId)
            loadWishlist(token)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                WishlistViewModel(application.container.wishlistRepository)
            }
        }
    }
}