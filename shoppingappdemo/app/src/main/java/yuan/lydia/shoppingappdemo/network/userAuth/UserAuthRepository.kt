package yuan.lydia.shoppingappdemo.network.userAuth

import yuan.lydia.shoppingappdemo.network.ApiServices

interface UserAuthRepository {
    suspend fun login(loginRequest: LoginRequest): LoginResponse
}

class NetworkUserAuthRepository(
    private val apiServices: ApiServices
) : UserAuthRepository {
    override suspend fun login(loginRequest: LoginRequest): LoginResponse = apiServices.login(loginRequest)
}