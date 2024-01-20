package yuan.lydia.shoppingappdemo.network.history

interface HistoryRepository {
    suspend fun getOrderHistory(token: String): OrderHistoryResponse
}

class NetworkHistoryRepository(
    private val historyApiServices: HistoryApiServices
) : HistoryRepository {
    override suspend fun getOrderHistory(token: String): OrderHistoryResponse = historyApiServices.getOrderHistory("Bearer $token")
}
