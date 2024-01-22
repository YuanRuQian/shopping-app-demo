package yuan.lydia.shoppingappdemo.data.wishlist

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
import yuan.lydia.shoppingappdemo.network.extractErrorBody
import yuan.lydia.shoppingappdemo.network.wishlist.AddToWishlistResponse
import yuan.lydia.shoppingappdemo.network.wishlist.DeleteFromWishlistResponse
import yuan.lydia.shoppingappdemo.network.wishlist.Product
import yuan.lydia.shoppingappdemo.network.wishlist.WishlistRepository

sealed interface AddToWishListUiState {
    data class Success(val response: AddToWishlistResponse) : AddToWishListUiState

    data class Error(val message: String) : AddToWishListUiState
}

sealed interface RemoveFromWishListUiState {
    data class Success(val response: DeleteFromWishlistResponse) : RemoveFromWishListUiState

    data class Error(val message: String) : RemoveFromWishListUiState

}

class WishlistViewModel(private val wishlistRepository: WishlistRepository) : ViewModel() {

    private val _wishlist = MutableLiveData<List<Product>>()
    val wishlist: LiveData<List<Product>>
        get() = _wishlist

    fun loadWishlist(token: String) {
        viewModelScope.launch {
            _wishlist.value = wishlistRepository.getWishlist(token).products
        }
    }

    private suspend fun addToWishlistHelper(token: String, productId: Long): AddToWishListUiState {
        return try {
            val response = wishlistRepository.addProductToWishlist(token, productId)
            AddToWishListUiState.Success(response)
        } catch (e: retrofit2.HttpException) {
            Log.d("WishlistViewModel", "addToWishlistHelper: ${e.message()}")
            AddToWishListUiState.Error(extractErrorBody(e))
        }
    }

    fun addToWishlistAndReloadWishlistData(
        token: String,
        productId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = addToWishlistHelper(token, productId)) {
                is AddToWishListUiState.Success -> {
                    onSuccess()
                    loadWishlist(token)
                }

                is AddToWishListUiState.Error -> {
                    onFailure(result.message)
                }
            }
        }
    }

    private suspend fun removeFromWishlistHelper(
        token: String,
        productId: Long
    ): RemoveFromWishListUiState {
        return try {
            val response = wishlistRepository.removeProductFromWishlist(token, productId)
            RemoveFromWishListUiState.Success(response)
        } catch (e: retrofit2.HttpException) {
            Log.d("WishlistViewModel", "removeFromWishlistHelper: ${e.message()}")
            RemoveFromWishListUiState.Error(extractErrorBody(e))
        }
    }

    fun removeFromWishlistAndReloadWishlistData(
        token: String,
        productId: Long,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = removeFromWishlistHelper(token, productId)) {
                is RemoveFromWishListUiState.Success -> {
                    onSuccess()
                    loadWishlist(token)
                }

                is RemoveFromWishListUiState.Error -> {
                    onFailure(result.message)
                }
            }
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