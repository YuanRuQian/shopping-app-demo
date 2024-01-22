package yuan.lydia.shoppingappdemo.ui.screens.userAuth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardType.Companion.Password
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import yuan.lydia.shoppingappdemo.data.userAuth.UiState
import yuan.lydia.shoppingappdemo.data.userAuth.UserAuthViewModel
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager

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
    var isLoginButtonEnabled by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val uiState by userAuthViewModel.uiState.observeAsState()

    fun updateLoginButtonState() {
        isLoginButtonEnabled = username.isNotBlank() && password.isNotBlank()
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

        ClickableText(
            modifier = Modifier.testTag("registerText"),
            text = AnnotatedString("Don't have an account? Click to register"),
            onClick = {
                Log.d("LoginScreen", "navigate from login to register")
                navigateToRegister()
            },
            style = MaterialTheme.typography.labelLarge
        )

        when (val loginUiState = uiState ?: UiState.Uninitialized) {
            is UiState.LoginSuccess -> {
                LaunchedEffect(loginUiState.response.status.message) {
                    isLoginButtonEnabled = true
                    val userInfoManager = UserInfoManager.getInstance(context)
                    userInfoManager.saveToken(loginUiState.response.token)
                    userInfoManager.saveUsername(username)
                    onLoginSuccess()
                    showSnackBarMessage(loginUiState.response.status.message)
                }
            }

            is UiState.LoginError -> {
                LaunchedEffect(loginUiState.response.status.message) {
                    showSnackBarMessage(loginUiState.response.status.message)
                }
                isLoginButtonEnabled = true
            }

            UiState.Loading -> {
                isLoginButtonEnabled = false
            }

            UiState.Uninitialized -> {
            }

            is UiState.RegisterError -> {
                LaunchedEffect(loginUiState.response.message) {
                    showSnackBarMessage(loginUiState.response.message)
                }
                isLoginButtonEnabled = true
            }

            is UiState.RegisterNetworkError -> {
                LaunchedEffect(loginUiState.message) {
                    showSnackBarMessage(loginUiState.message)
                }
                isLoginButtonEnabled = true
            }

            is UiState.LoginNetworkError -> {
                LaunchedEffect(loginUiState.message) {
                    showSnackBarMessage(loginUiState.message)
                }
                isLoginButtonEnabled = true
            }
        }
    }
}
