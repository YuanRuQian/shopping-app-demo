package yuan.lydia.shoppingappdemo.network


import retrofit2.http.Body
import retrofit2.http.POST
import yuan.lydia.shoppingappdemo.network.userAuth.LoginRequest
import yuan.lydia.shoppingappdemo.network.userAuth.LoginResponse

interface ApiServices {
    @POST("/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}