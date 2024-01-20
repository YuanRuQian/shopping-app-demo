package yuan.lydia.shoppingappdemo.network.shopping

import com.google.gson.annotations.SerializedName

data class ProductsResponse(
    @field:SerializedName("status") val status: Status,
    @field:SerializedName("products") val products: List<Product>,
)

data class Status(
    @field:SerializedName("success") val success: Boolean,
    @field:SerializedName("message") val message: String,
)

data class Product(
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("description") val description: String,
    @field:SerializedName("retailPrice") val retailPrice: Double,
)
