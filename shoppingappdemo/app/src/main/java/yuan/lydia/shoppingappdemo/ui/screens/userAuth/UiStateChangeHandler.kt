package yuan.lydia.shoppingappdemo.ui.screens.userAuth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import yuan.lydia.shoppingappdemo.data.userAuth.UiState
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.ui.common.FailureDialog

@Composable
fun UiStateChangeHandler(
    openAlertDialog: Boolean,
    uiState: UiState,
    setOpenAlertDialog: (Boolean) -> Unit,
    alertDialogMessage: String,
    setIsButtonEnabled: (Boolean) -> Unit,
    onLoginSuccess: () -> Unit,
    showSnackBarMessage: (String) -> Unit,
    setAlertDialogMessage: (String) -> Unit,
    username: String
) {
    val context = LocalContext.current
    when (openAlertDialog) {
        true -> {
            FailureDialog(
                onDismissRequest = { setOpenAlertDialog(false) },
                errorMessage = alertDialogMessage
            )
        }

        else -> {}
    }

    when (uiState) {
        is UiState.LoginSuccess -> {
            LaunchedEffect(uiState.response.status.message) {
                Log.d("UiStateChangeHandler", "LoginSuccess: ${uiState.response.status.message}")
                setIsButtonEnabled(true)
                val userInfoManager = UserInfoManager.getInstance(context)
                userInfoManager.saveToken(uiState.response.token)
                userInfoManager.saveUsername(username)
                onLoginSuccess()
                showSnackBarMessage(uiState.response.status.message)
            }
        }

        is UiState.LoginError -> {
            LaunchedEffect(uiState.response.status.message) {
                Log.d("UiStateChangeHandler", "LoginError: ${uiState.response.status.message}")
                setAlertDialogMessage(uiState.response.status.message)
                setOpenAlertDialog(true)
            }
            setIsButtonEnabled(true)
        }

        UiState.Loading -> {
            setIsButtonEnabled(false)
        }

        UiState.Uninitialized -> {
        }

        is UiState.RegisterError -> {
            LaunchedEffect(uiState.response.message) {
                Log.d("UiStateChangeHandler", "RegisterError: ${uiState.response.message}")
                setAlertDialogMessage(uiState.response.message)
                setOpenAlertDialog(true)
            }
            setIsButtonEnabled(true)
        }

        is UiState.RegisterNetworkError -> {
            LaunchedEffect(uiState.message) {
                Log.d("UiStateChangeHandler", "RegisterNetworkError: ${uiState.message}")
                setAlertDialogMessage(uiState.message)
                setOpenAlertDialog(true)
            }
            setIsButtonEnabled(true)
        }

        is UiState.LoginNetworkError -> {
            LaunchedEffect(uiState.message) {
                Log.d("UiStateChangeHandler", "LoginNetworkError: ${uiState.message}")
                setAlertDialogMessage(uiState.message)
                setOpenAlertDialog(true)
            }
            setIsButtonEnabled(true)
        }
    }
}