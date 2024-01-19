package yuan.lydia.shoppingappdemo.network.userAuth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

