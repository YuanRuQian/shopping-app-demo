package yuan.lydia.shoppingappdemo.network.history

interface HistoryRepository {
    suspend fun getOrderHistory(token: String): OrderHistoryResponse

    suspend fun getOrderDetails(token: String, orderId: String): OrderDetailsResponse
}

// TODO: pre set interceptor to add token for all in-app APIs
class NetworkHistoryRepository(
    private val historyApiServices: HistoryApiServices
) : HistoryRepository {
    override suspend fun getOrderHistory(token: String): OrderHistoryResponse =
        historyApiServices.getOrderHistory("Bearer $token")

    override suspend fun getOrderDetails(token: String, orderId: String): OrderDetailsResponse =
        historyApiServices.getOrderDetails("Bearer $token", orderId)

}
