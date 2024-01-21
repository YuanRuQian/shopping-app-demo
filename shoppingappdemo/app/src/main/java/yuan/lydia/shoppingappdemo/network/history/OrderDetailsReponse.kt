package yuan.lydia.shoppingappdemo.network.history

data class OrderDetailsResponse(
    val status: OrderStatus,
    val order: OrderDetail,
)

data class OrderStatus(
    val success: Boolean,
    val message: String,
)

data class OrderDetail(
    val id: Long,
    val orderStatus: String,
    val datePlaced: Long,
    val orderItems: List<OrderItem>,
)

data class OrderItem(
    val id: Long,
    val product: Product,
    val purchasedPrice: Double,
    val quantity: Long,
)

data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val retailPrice: Double,
)
