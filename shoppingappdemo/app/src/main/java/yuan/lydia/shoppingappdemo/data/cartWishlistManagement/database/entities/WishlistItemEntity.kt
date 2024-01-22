package yuan.lydia.shoppingappdemo.data.cartWishlistManagement.database.entities

import androidx.room.Entity

@Entity(
    primaryKeys = ["username", "productId"],
    tableName = "wishlistItem"
)
data class WishlistItemEntity(
    val username: String,
    val productId: Long
)