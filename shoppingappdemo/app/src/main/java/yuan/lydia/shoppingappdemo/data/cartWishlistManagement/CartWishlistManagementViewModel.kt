package yuan.lydia.shoppingappdemo.data.cartWishlistManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import yuan.lydia.shoppingappdemo.ShoppingApplication
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.repository.CartWishlistManagementRepository

class CartWishlistManagementViewModel(private val cartWishlistManagementRepository: CartWishlistManagementRepository) :
    ViewModel() {


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