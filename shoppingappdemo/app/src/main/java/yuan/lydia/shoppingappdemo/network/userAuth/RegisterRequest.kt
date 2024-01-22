package yuan.lydia.shoppingappdemo.network.userAuth

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
)
