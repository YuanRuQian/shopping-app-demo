package yuan.lydia.shoppingappdemo.network.cart

data class OrderRequest(
    val order: List<Order>,
)

data class Order(
    val productId: Long,
    val quantity: Long,
)
