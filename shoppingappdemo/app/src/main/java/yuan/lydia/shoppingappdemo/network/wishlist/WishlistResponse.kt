package yuan.lydia.shoppingappdemo.network.wishlist

import yuan.lydia.shoppingappdemo.network.common.Product

data class WishlistResponse(
    val status: Status,
    val products: List<Product>,
)

data class Status(
    val success: Boolean,
    val message: String,
)

