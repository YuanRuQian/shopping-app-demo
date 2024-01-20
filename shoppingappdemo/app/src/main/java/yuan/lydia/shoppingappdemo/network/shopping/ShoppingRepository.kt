package yuan.lydia.shoppingappdemo.network.shopping

interface ShoppingRepository {
    suspend fun getProducts(token: String): ProductsResponse
}

class NetworkShoppingRepository(
    private val shoppingApiServices: ShoppingApiServices
) : ShoppingRepository {
    override suspend fun getProducts(token: String): ProductsResponse =
        shoppingApiServices.getProducts("Bearer $token")
}
