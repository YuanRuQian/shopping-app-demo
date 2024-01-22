package yuan.lydia.shoppingappdemo.network.userAuth


import retrofit2.http.Body
import retrofit2.http.POST

interface UserAuthApiServices {
    @POST("/users/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("/users")
    suspend fun register(@Body loginRequest: RegisterRequest): RegisterResponse
}