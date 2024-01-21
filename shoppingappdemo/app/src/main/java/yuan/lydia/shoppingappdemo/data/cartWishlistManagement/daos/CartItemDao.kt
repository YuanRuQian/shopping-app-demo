package yuan.lydia.shoppingappdemo.data.cartWishlistManagement.daos


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.CartItemEntity

@Dao
interface CartItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCartItem(cartItem: CartItemEntity)

    @Query("SELECT * FROM cartItem WHERE username = :username")
    fun getUserCartItems(username: String): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cartItem WHERE username = :username AND productId = :productId")
    fun getCartItemInfo(username: String, productId: String): Flow<CartItemEntity?>

    @Query("SELECT * FROM cartItem WHERE username = :username AND productId = :productId")
    suspend fun getCartItem(username: String, productId: String): CartItemEntity?

    @Transaction
    suspend fun increaseQuantity(username: String, productId: String, addedQuantity: Int) {
        val cartItem = getCartItem(username, productId)
        if (cartItem != null) {
            cartItem.quantity += addedQuantity
        } else {
            // Product not added before, create a new entry
            insertCartItem(CartItemEntity(username, productId, addedQuantity))
        }
        // Update the quantity regardless of whether the item was present or not
        insertCartItem(cartItem ?: CartItemEntity(username, productId, addedQuantity))
    }

    @Transaction
    suspend fun reduceQuantity(username: String, productId: String, reducedQuantity: Int) {
        val cartItem = getCartItem(username, productId)
        if (cartItem != null) {
            cartItem.quantity = maxOf(cartItem.quantity - reducedQuantity, 0)
            insertCartItem(cartItem)
        }
    }

    @Query("DELETE FROM cartItem WHERE username = :username")
    suspend fun clearCart(username: String)
}
