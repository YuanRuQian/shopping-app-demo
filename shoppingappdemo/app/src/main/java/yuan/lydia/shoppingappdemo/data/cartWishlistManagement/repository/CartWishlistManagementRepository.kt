package yuan.lydia.shoppingappdemo.data.cartWishlistManagement.repository

import kotlinx.coroutines.flow.Flow
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.CartItemEntity
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.WishlistItemEntity

interface CartWishlistManagementRepository {

    fun getCartItemInfo(username: String, productId: Long): Flow<CartItemEntity?>

    suspend fun updateQuantity(username: String, productId: Long, newQuantity: Int)

    suspend fun increaseQuantity(username: String, productId: Long, addedQuantity: Int)

    fun loadUserCartData(username: String): Flow<List<CartItemEntity>>

    suspend fun clearCart(username: String)

    fun getWishlistItemInfo(username: String, productId: Long): Flow<CartItemEntity?>

    suspend fun addToWishlist(username: String, productId: Long)

    suspend fun removeFromWishlist(username: String, productId: Long)

    fun loadUserWishlistData(username: String): Flow<List<WishlistItemEntity>>

    suspend fun clearWishlistItem(username: String, productId: Long)
}