package yuan.lydia.shoppingappdemo.network


import retrofit2.http.Body
import retrofit2.http.POST
import yuan.lydia.shoppingappdemo.network.userAuth.LoginRequest
import yuan.lydia.shoppingappdemo.network.userAuth.LoginResponse
import yuan.lydia.shoppingappdemo.network.userAuth.RegisterRequest
import yuan.lydia.shoppingappdemo.network.userAuth.RegisterResponse

interface ApiServices {
    @POST("/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("/users")
    suspend fun register(@Body loginRequest: RegisterRequest): RegisterResponse
}