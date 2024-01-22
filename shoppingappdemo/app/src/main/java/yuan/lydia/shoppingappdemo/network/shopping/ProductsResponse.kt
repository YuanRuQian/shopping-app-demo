package yuan.lydia.shoppingappdemo.network.shopping

data class ProductsResponse(
    val status: Status,
    val products: List<Product>,
)

data class Status(
    val success: Boolean,
    val message: String,
)

data class Product(
    val id: Long,
    val name: String,
    val description: String,
    val retailPrice: Double,
)
