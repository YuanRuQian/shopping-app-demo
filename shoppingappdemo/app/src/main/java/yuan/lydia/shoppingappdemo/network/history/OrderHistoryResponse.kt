package yuan.lydia.shoppingappdemo.network.history

data class OrderHistoryResponse(
    val status: Status,
    val orders: List<Order>,
)

data class Status(
    val success: Boolean,
    val message: String,
)

data class Order(
    val orderId: Long,
    val userId: Long,
    val username: String,
    val orderStatus: String,
    val datePlaced: Long,
)
