package yuan.lydia.shoppingappdemo.data.userAuth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.launch
import yuan.lydia.shoppingappdemo.ShoppingApplication
import yuan.lydia.shoppingappdemo.network.userAuth.LoginRequest
import yuan.lydia.shoppingappdemo.network.userAuth.LoginResponse
import yuan.lydia.shoppingappdemo.network.userAuth.RegisterRequest
import yuan.lydia.shoppingappdemo.network.userAuth.RegisterResponse
import yuan.lydia.shoppingappdemo.network.userAuth.UserAuthRepository

// TODO: handle error notification and UI state change
// TODO: store token in shared preference

sealed interface UiState {
    data class LoginSuccess(val response: LoginResponse) : UiState

    data class LoginError(val response: LoginResponse) : UiState

    data class LoginNetworkError(val message: String) : UiState

    data class RegisterError(val response: RegisterResponse) : UiState

    data class RegisterNetworkError(val message: String) : UiState

    data object Uninitialized : UiState

    data object Loading : UiState
}

class UserAuthViewModel(
    val userAuthRepository: UserAuthRepository
) : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState.Uninitialized)


    private suspend fun loginHelper(loginRequest: LoginRequest): UiState {
        return try {
            val response = userAuthRepository.login(loginRequest)
            if (response.status.success) {
                Log.d("UserAuthViewModel", "login success: $response")
                UiState.LoginSuccess(response)
            } else {
                Log.d("UserAuthViewModel", "login error: $response")
                UiState.LoginError(response)
            }
        } catch (e: retrofit2.HttpException) {
            Log.d("UserAuthViewModel", "login error: $e")
            UiState.LoginNetworkError(e.message())
        }
    }

    fun login(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)
        uiState = UiState.Loading
        viewModelScope.launch {
            uiState = loginHelper(loginRequest)
        }
    }

    fun register(username: String, email: String, password: String) {
        val registerRequest = RegisterRequest(username, email, password)
        uiState = UiState.Loading
        viewModelScope.launch {
            uiState = try {
                val response = userAuthRepository.register(registerRequest)
                if (response.success) {
                    Log.d("UserAuthViewModel", "register success: $response")
                    loginHelper(LoginRequest(username, password))
                } else {
                    Log.d("UserAuthViewModel", "register error: $response")
                    UiState.RegisterError(response)
                }
            } catch (e: retrofit2.HttpException) {
                Log.d("UserAuthViewModel", "register error: $e")
                UiState.RegisterNetworkError(e.message())
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication)
                val artworkRepository = application.container.userAuthRepository
                UserAuthViewModel(userAuthRepository = artworkRepository)
            }
        }
    }
}