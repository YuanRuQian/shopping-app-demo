package yuan.lydia.shoppingappdemo.network.userAuth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val status: Status,
    val token: String,
)

@Serializable
data class Status(
    val success: Boolean,
    val message: String,
)

