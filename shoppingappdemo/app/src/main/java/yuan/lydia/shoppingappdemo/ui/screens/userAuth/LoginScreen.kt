package yuan.lydia.shoppingappdemo.ui.screens.userAuth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardType.Companion.Password
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import yuan.lydia.shoppingappdemo.data.userAuth.UiState
import yuan.lydia.shoppingappdemo.data.userAuth.UserAuthViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    navigateToRegister: () -> Unit,
    showSnackBarMessage: (String) -> Unit,
    userAuthViewModel: UserAuthViewModel = viewModel(factory = UserAuthViewModel.Factory)
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val (isLoginButtonEnabled, setIsLoginButtonEnabled) = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by userAuthViewModel.uiState.observeAsState()
    val (openAlertDialog, setOpenAlertDialog) = remember { mutableStateOf(false) }
    val (alertDialogMessage, setAlertDialogMessage) = remember { mutableStateOf("") }

    fun updateLoginButtonState() {
        setIsLoginButtonEnabled(username.isNotBlank() && password.isNotBlank())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Username TextField
        TextField(
            modifier = Modifier.testTag("username"),
            value = username,
            onValueChange = {
                username = it
                updateLoginButtonState()
            },
            label = { Text(text = "Username") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        Spacer(modifier = Modifier.padding(16.dp))

        TextField(
            modifier = Modifier.testTag("password"),
            value = password,
            onValueChange = {
                password = it
                updateLoginButtonState()
            },
            singleLine = true,
            label = { Text("Password") },
            visualTransformation =
            if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = Password),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    val visibilityIcon =
                        if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                    val description = if (isPasswordVisible) "Hide password" else "Show password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            }
        )

        Spacer(modifier = Modifier.padding(32.dp))

        Button(
            onClick = {
                userAuthViewModel.login(username, password)
                keyboardController?.hide()
            },
            enabled = isLoginButtonEnabled,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .testTag("loginButton"),
        ) {
            Text(text = "Login")
        }

        TextButton(
            modifier = Modifier.testTag("registerText"),
            onClick = {
                Log.d("LoginScreen", "navigate from login to register")
                navigateToRegister()
            }) {
            Text(text = "Don't have an account? Click here to register")
        }

        UiStateChangeHandler(
            openAlertDialog = openAlertDialog,
            uiState = uiState ?: UiState.Uninitialized,
            setOpenAlertDialog = setOpenAlertDialog,
            alertDialogMessage = alertDialogMessage,
            setIsButtonEnabled = setIsLoginButtonEnabled,
            onLoginSuccess = onLoginSuccess,
            showSnackBarMessage = showSnackBarMessage,
            setAlertDialogMessage = setAlertDialogMessage,
            username = username
        )
    }
}
