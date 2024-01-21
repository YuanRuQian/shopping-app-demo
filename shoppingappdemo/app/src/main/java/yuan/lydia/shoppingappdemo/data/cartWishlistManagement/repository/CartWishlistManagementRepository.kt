package yuan.lydia.shoppingappdemo.data.cartWishlistManagement.repository

import kotlinx.coroutines.flow.Flow
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.CartItemEntity
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.WishlistItemEntity

interface CartWishlistManagementRepository {

    fun getCartItemInfo(username: String, productId: String): Flow<CartItemEntity?>

    suspend fun increaseQuantity(username: String, productId: String, addedQuantity: Int)

    suspend fun reduceQuantity(username: String, productId: String, reducedQuantity: Int)

    fun getUserCart(username: String): Flow<List<CartItemEntity>>

    suspend fun clearCart(username: String)

    fun getWishlistItemInfo(username: String, productId: String): Flow<CartItemEntity?>

    suspend fun addToWishlist(username: String, productId: String)

    suspend fun removeFromWishlist(username: String, productId: String)

    fun getUserWishlist(username: String): Flow<List<WishlistItemEntity>>

    suspend fun clearWishlistItem(username: String, productId: String)
}