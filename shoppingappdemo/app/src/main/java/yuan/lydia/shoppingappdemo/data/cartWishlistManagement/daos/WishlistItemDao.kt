package yuan.lydia.shoppingappdemo.data.cartWishlistManagement.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.WishlistItemEntity


@Dao
interface WishlistItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(wishlistItem: WishlistItemEntity)
    @Delete
    suspend fun removeWishlistItem(wishlistItem: WishlistItemEntity)

    @Query("SELECT * FROM wishlistItem WHERE username = :username AND productId = :productId")
    suspend fun getWishlistItem(username: String, productId: Long): WishlistItemEntity?

    @Query("SELECT * FROM wishlistItem WHERE username = :username")
    fun getUserWishlistItems(username: String): Flow<List<WishlistItemEntity>>

    @Transaction
    suspend fun addProductToWishlist(username: String, productId: Long) {
        insertWishlistItem(WishlistItemEntity(username, productId))
    }

    @Transaction
    suspend fun removeProductFromWishlist(username: String, productId: Long) {
        val wishlistItem = getWishlistItem(username, productId)
        if (wishlistItem != null) {
            removeWishlistItem(wishlistItem)
        }
    }
}




