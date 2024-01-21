package yuan.lydia.shoppingappdemo.network.history

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface HistoryApiServices {
    @GET("/orders")
    suspend fun getOrderHistory(@Header("Authorization") tokenHeader: String): OrderHistoryResponse
    @GET("/orders/{id}")
    suspend fun getOrderDetails(
        @Header("Authorization") tokenHeader: String,
         @Path("id") orderId: String
    ): OrderDetailsResponse
}