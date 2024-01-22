package yuan.lydia.shoppingappdemo.data.cart.repository

import kotlinx.coroutines.flow.Flow
import yuan.lydia.shoppingappdemo.data.cart.database.CartItemEntity
import yuan.lydia.shoppingappdemo.network.cart.OrderRequest
import yuan.lydia.shoppingappdemo.network.cart.OrderResponse

interface CartManagementRepository {

    suspend fun submitOrder(token: String, orderRequest: OrderRequest): OrderResponse

    fun getCartItemInfo(username: String, productId: Long): Flow<CartItemEntity?>

    suspend fun updateQuantity(username: String, productId: Long, newQuantity: Int)

    suspend fun increaseQuantity(username: String, productId: Long, addedQuantity: Int)

    fun loadUserCartData(username: String): Flow<List<CartItemEntity>>

    suspend fun clearCart(username: String)
}