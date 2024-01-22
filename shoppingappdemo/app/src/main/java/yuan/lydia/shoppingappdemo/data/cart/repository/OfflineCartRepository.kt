package yuan.lydia.shoppingappdemo.data.cart.repository

import kotlinx.coroutines.flow.Flow
import yuan.lydia.shoppingappdemo.data.cart.database.CartItemDao
import yuan.lydia.shoppingappdemo.data.cart.database.CartItemEntity
import yuan.lydia.shoppingappdemo.network.cart.CartApiServices
import yuan.lydia.shoppingappdemo.network.cart.OrderRequest
import yuan.lydia.shoppingappdemo.network.cart.OrderResponse

class OfflineCartRepository(
    private val cartItemDao: CartItemDao,
    private val cartApiServices: CartApiServices
) : CartRepository {
    override suspend fun submitOrder(token: String, orderRequest: OrderRequest): OrderResponse =
        cartApiServices.submitOrder(orderRequest, "Bearer $token")

    override fun getCartItemInfo(username: String, productId: Long): Flow<CartItemEntity?> =
        cartItemDao.getCartItemInfo(username, productId)

    override suspend fun updateQuantity(username: String, productId: Long, newQuantity: Int) =
        cartItemDao.updateQuantity(username, productId, newQuantity)

    override suspend fun increaseQuantity(username: String, productId: Long, addedQuantity: Int) =
        cartItemDao.increaseQuantity(username, productId, addedQuantity)

    override fun loadUserCartData(username: String): Flow<List<CartItemEntity>> =
        cartItemDao.getUserCartItems(username)

    override suspend fun clearCart(username: String) = cartItemDao.clearCart(username)
}