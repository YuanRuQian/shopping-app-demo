package yuan.lydia.shoppingappdemo.data.userAuth

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
import yuan.lydia.shoppingappdemo.network.userAuth.UserAuthRepository

sealed interface UiState {
    data class LoginSuccess(val artwork: LoginResponse) : UiState
    object Error : UiState
    object Loading : UiState

    object Uninitialized : UiState
    object InvalidInput : UiState
}

class UserAuthViewModel(
    val userAuthRepository: UserAuthRepository
) : ViewModel() {
    var uiState: UiState by mutableStateOf(UiState.Uninitialized)
        private set

    fun login(username: String, password: String) {
        val loginRequest = LoginRequest(username, password)
        uiState = UiState.Loading
        // Create a new ViewModel scope to asynchronously fetch data
        viewModelScope.launch {
            // Using a try/catch to handle possible Exception when updating state
            uiState = try {
                UiState.LoginSuccess(userAuthRepository.login(loginRequest))
            } catch (e: retrofit2.HttpException) {
                UiState.Error
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