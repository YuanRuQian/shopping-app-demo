package yuan.lydia.shoppingappdemo.data.cart.database

import androidx.room.Entity

@Entity(
    primaryKeys = ["username", "productId"],
    tableName = "cartItem",
)
data class CartItemEntity(
    val username: String,
    val productId: Long,
    var quantity: Int
)
