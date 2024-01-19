package yuan.lydia.shoppingappdemo.network.userAuth

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class LoginResponse(
    @field:SerializedName("status") val status: Status,
    @field:SerializedName("token") val token: String,
)

@Serializable
data class Status(
    @field:SerializedName("success") val success: Boolean,
    @field:SerializedName("message") val message: String,
)

