package yuan.lydia.shoppingappdemo.network.userAuth

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @field:SerializedName("success") val success: Boolean,
    @field:SerializedName("message") val message: String,
)
