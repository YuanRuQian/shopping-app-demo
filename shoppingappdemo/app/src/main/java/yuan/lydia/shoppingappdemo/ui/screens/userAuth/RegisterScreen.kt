package yuan.lydia.shoppingappdemo.ui.screens.userAuth

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import yuan.lydia.shoppingappdemo.ui.common.isEmailValid

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    onLoginSuccess: () -> Unit,
    navigateToLogin: () -> Unit,
    showSnackBarMessage: (String) -> Unit
) {
    val userAuthViewModel: UserAuthViewModel = viewModel(factory = UserAuthViewModel.Factory)
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val (isEmailValid, setIsEmailValid) = remember { mutableStateOf(true) }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val (isRegisterButtonEnabled, setIsRegisterButtonEnabled) = remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val uiState by userAuthViewModel.uiState.observeAsState()
    val (openAlertDialog, setOpenAlertDialog) = remember { mutableStateOf(false) }
    val (alertDialogMessage, setAlertDialogMessage) = remember { mutableStateOf("") }

    fun updateLoginButtonState() {
        setIsRegisterButtonEnabled(
            username.isNotBlank() && email.isNotBlank() && password.isNotBlank() && isEmailValid
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
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
            value = email,
            onValueChange = {
                email = it
                setIsEmailValid(isEmailValid(email))
                updateLoginButtonState()
            },
            label = { Text(if (!isEmailValid) "Email*" else "Email") },
            singleLine = true,
            supportingText = {
                if (!isEmailValid) {
                    Text(
                        text = "Please enter a valid email address",
                    )
                }
            },
            isError = !isEmailValid,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.padding(16.dp))

        TextField(
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
                    val description =
                        if (isPasswordVisible) "Hide password" else "Show password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            }
        )

        Spacer(modifier = Modifier.padding(32.dp))

        Button(
            onClick = {
                userAuthViewModel.register(username, email, password)
                keyboardController?.hide()
            },
            enabled = isRegisterButtonEnabled,
            modifier = Modifier
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Register")
        }

        TextButton(
            modifier = Modifier.testTag("loginText"),
            onClick = {
                Log.d("RegisterScreen", "navigate from register to login")
                navigateToLogin()
            }
        ) {
            Text(text = "Already have an account? Click here to login")
        }

        UiStateChangeHandler(
            openAlertDialog = openAlertDialog,
            uiState = uiState ?: UiState.Uninitialized,
            setOpenAlertDialog = setOpenAlertDialog,
            alertDialogMessage = alertDialogMessage,
            setIsButtonEnabled = setIsRegisterButtonEnabled,
            onLoginSuccess = onLoginSuccess,
            showSnackBarMessage = showSnackBarMessage,
            setAlertDialogMessage = setAlertDialogMessage,
            username = username
        )
    }
}