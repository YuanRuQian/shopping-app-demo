package yuan.lydia.shoppingappdemo.network.shopping

import retrofit2.http.GET
import retrofit2.http.Header


interface ShoppingApiServices {
    @GET("/products")
    suspend fun getProducts(@Header("Authorization") tokenHeader: String): ProductsResponse
}
