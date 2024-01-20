package yuan.lydia.shoppingappdemo.network.userAuth

import yuan.lydia.shoppingappdemo.network.ApiServices

interface UserAuthRepository {
    suspend fun login(loginRequest: LoginRequest): LoginResponse
    suspend fun register(registerRequest: RegisterRequest): RegisterResponse
}

class NetworkUserAuthRepository(
    private val apiServices: ApiServices
) : UserAuthRepository {
    override suspend fun login(loginRequest: LoginRequest): LoginResponse = apiServices.login(loginRequest)
    override suspend fun register(registerRequest: RegisterRequest): RegisterResponse = apiServices.register(registerRequest)
}