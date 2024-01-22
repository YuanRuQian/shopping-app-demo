package yuan.lydia.shoppingappdemo.network.wishlist

interface WishlistRepository {
    suspend fun getWishlist(token: String): WishlistResponse

    suspend fun addProductToWishlist(token: String, productId: Long): AddToWishlistResponse

    suspend fun removeProductFromWishlist(
        token: String,
        productId: Long
    ): DeleteFromWishlistResponse
}

class NetworkWishlistRepository(
    private val wishlistApiServices: WishlistApiServices
) : WishlistRepository {
    override suspend fun getWishlist(token: String): WishlistResponse =
        wishlistApiServices.getWishlist("Bearer $token")

    override suspend fun addProductToWishlist(
        token: String,
        productId: Long
    ): AddToWishlistResponse {
        val addToWishlistRequest = AddToWishlistRequest(productId.toString())
        return wishlistApiServices.addToWishlist(addToWishlistRequest, "Bearer $token")
    }

    override suspend fun removeProductFromWishlist(
        token: String,
        productId: Long
    ): DeleteFromWishlistResponse =
        wishlistApiServices.deleteFromWishlist("Bearer $token", productId)

}
