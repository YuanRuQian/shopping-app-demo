package yuan.lydia.shoppingappdemo.data.cartWishlistManagement.repository

import kotlinx.coroutines.flow.Flow
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.database.daos.CartItemDao
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.database.daos.WishlistItemDao
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.database.entities.CartItemEntity
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.database.entities.WishlistItemEntity
import yuan.lydia.shoppingappdemo.network.cart.CartApiServices
import yuan.lydia.shoppingappdemo.network.cart.OrderRequest
import yuan.lydia.shoppingappdemo.network.cart.OrderResponse

class OfflineCartWishlistManagementRepository(
    private val cartItemDao: CartItemDao,
    private val wishlistItemDao: WishlistItemDao,
    private val cartApiServices: CartApiServices
): CartWishlistManagementRepository {
    override suspend fun submitOrder(token: String, orderRequest: OrderRequest): OrderResponse = cartApiServices.submitOrder(orderRequest, "Bearer $token")

    override fun getCartItemInfo(username: String, productId: Long): Flow<CartItemEntity?> = cartItemDao.getCartItemInfo(username, productId)
    override suspend fun updateQuantity(username: String, productId: Long, newQuantity: Int) = cartItemDao.updateQuantity(username, productId, newQuantity)
    override suspend fun increaseQuantity(username: String, productId: Long, addedQuantity: Int) = cartItemDao.increaseQuantity(username, productId, addedQuantity)
    
    override fun loadUserCartData(username: String): Flow<List<CartItemEntity>> = cartItemDao.getUserCartItems(username)

    override suspend fun clearCart(username: String) = cartItemDao.clearCart(username)

    override fun getWishlistItemInfo(username: String, productId: Long): Flow<CartItemEntity?> = cartItemDao.getCartItemInfo(username, productId)

    override suspend fun addToWishlist(username: String, productId: Long) = wishlistItemDao.addProductToWishlist(username, productId)

    override suspend fun removeFromWishlist(username: String, productId: Long) = wishlistItemDao.removeProductFromWishlist(username, productId)


    override fun loadUserWishlistData(username: String): Flow<List<WishlistItemEntity>> = wishlistItemDao.getUserWishlistItems(username)

    override suspend fun clearWishlistItem(username: String, productId: Long) = wishlistItemDao.removeProductFromWishlist(username, productId)
}