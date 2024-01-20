package yuan.lydia.shoppingappdemo.network.history

import retrofit2.http.GET
import retrofit2.http.Header

interface HistoryApiServices {
    @GET("/orders")
    suspend fun getOrderHistory(@Header("Authorization") tokenHeader: String): OrderHistoryResponse
}