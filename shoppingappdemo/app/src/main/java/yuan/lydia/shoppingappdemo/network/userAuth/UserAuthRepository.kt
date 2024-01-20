package yuan.lydia.shoppingappdemo.network.userAuth

interface UserAuthRepository {
    suspend fun login(loginRequest: LoginRequest): LoginResponse
    suspend fun register(registerRequest: RegisterRequest): RegisterResponse
}

class NetworkUserAuthRepository(
    private val userAuthApiServices: UserAuthApiServices
) : UserAuthRepository {
    override suspend fun login(loginRequest: LoginRequest): LoginResponse = userAuthApiServices.login(loginRequest)
    override suspend fun register(registerRequest: RegisterRequest): RegisterResponse = userAuthApiServices.register(registerRequest)
}