package yuan.lydia.shoppingappdemo.network.userAuth

data class LoginResponse(
    val status: Status,
    val token: String,
)

data class Status(
    val success: Boolean,
    val message: String,
)

