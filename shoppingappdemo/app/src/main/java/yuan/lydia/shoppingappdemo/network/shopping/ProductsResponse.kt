package yuan.lydia.shoppingappdemo.network.shopping

import yuan.lydia.shoppingappdemo.network.common.Product

data class ProductsResponse(
    val status: Status,
    val products: List<Product>,
)

data class Status(
    val success: Boolean,
    val message: String,
)