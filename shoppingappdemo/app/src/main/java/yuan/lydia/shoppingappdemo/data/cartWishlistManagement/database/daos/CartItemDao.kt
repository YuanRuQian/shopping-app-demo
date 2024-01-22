package yuan.lydia.shoppingappdemo.data.cartWishlistManagement.database.daos


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.database.entities.CartItemEntity

@Dao
interface CartItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItemEntity)

    @Query("SELECT * FROM cartItem WHERE username = :username")
    fun getUserCartItems(username: String): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cartItem WHERE username = :username AND productId = :productId")
    fun getCartItemInfo(username: String, productId: Long): Flow<CartItemEntity?>

    @Query("SELECT * FROM cartItem WHERE username = :username AND productId = :productId")
    suspend fun getCartItem(username: String, productId: Long): CartItemEntity?

    @Transaction
    suspend fun increaseQuantity(username: String, productId: Long, addedQuantity: Int) {
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
    suspend fun updateQuantity(username: String, productId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            // If the updated quantity is 0 or less, remove the entry
            removeCartItem(username, productId)
        } else {
            val cartItem = getCartItem(username, productId)
            if (cartItem != null) {
                cartItem.quantity = newQuantity
                insertCartItem(cartItem)
            } else {
                // Product not added before, create a new entry
                insertCartItem(CartItemEntity(username, productId, newQuantity))
            }
        }
    }

    @Query("DELETE FROM cartItem WHERE username = :username AND productId = :productId")
    suspend fun removeCartItem(username: String, productId: Long)


    @Query("DELETE FROM cartItem WHERE username = :username")
    suspend fun clearCart(username: String)
}
