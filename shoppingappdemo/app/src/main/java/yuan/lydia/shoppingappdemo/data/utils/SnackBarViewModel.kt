package yuan.lydia.shoppingappdemo.data.utils

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SnackbarViewModel : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            Log.d("SnackbarViewModel", "showSnackbar: $message")
            _snackbarMessage.emit(message)
        }
    }

    fun clearSnackbar() {
        viewModelScope.launch {
            _snackbarMessage.emit(null)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SnackbarViewModel()
            }
        }
    }
}
