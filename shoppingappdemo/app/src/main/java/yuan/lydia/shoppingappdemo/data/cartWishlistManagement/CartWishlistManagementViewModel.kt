package yuan.lydia.shoppingappdemo.data.cartWishlistManagement

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
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.repository.CartWishlistManagementRepository

class CartWishlistManagementViewModel(private val cartWishlistManagementRepository: CartWishlistManagementRepository) :
    ViewModel() {

    private val _userCartLiveData = MutableLiveData<List<CartItemEntity>>()
    val userCartLiveData: LiveData<List<CartItemEntity>> get() = _userCartLiveData

    fun loadUserCartData(username: String) {
        viewModelScope.launch {
            cartWishlistManagementRepository.loadUserCartData(username).collect {
                _userCartLiveData.value = it
            }
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