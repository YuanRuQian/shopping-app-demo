package yuan.lydia.shoppingappdemo.network.wishlist

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface WishlistApiServices {
    @GET("/watchlist/products")
    suspend fun getWishlist(@Header("Authorization") tokenHeader: String): WishlistResponse

    @POST("/watchlist/products")
    suspend fun addToWishlist(
        @Body addToWishlistRequest: AddToWishlistRequest,
        @Header("Authorization") tokenHeader: String
    ): AddToWishlistResponse

    @DELETE("/watchlist/products/{id}")
    suspend fun deleteFromWishlist(
        @Header("Authorization") tokenHeader: String,
        @Path("id") id: Long
    ): DeleteFromWishlistResponse
}