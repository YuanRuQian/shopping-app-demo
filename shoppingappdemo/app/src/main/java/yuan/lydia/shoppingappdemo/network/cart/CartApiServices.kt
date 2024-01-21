package yuan.lydia.shoppingappdemo.network.cart

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CartApiServices {
    @POST("/orders")
    suspend fun submitOrder(@Body orderRequest: OrderRequest, @Header("Authorization") tokenHeader: String): OrderResponse
}