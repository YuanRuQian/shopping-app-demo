package yuan.lydia.shoppingappdemo.ui.screens

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import yuan.lydia.shoppingappdemo.data.utils.SnackbarViewModel
import yuan.lydia.shoppingappdemo.ui.screens.shopping.ProductsScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.LoginScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.RegisterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCanvas() {
    val snackbarViewModel: SnackbarViewModel = viewModel()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observe changes in the snackbarMessage using LaunchedEffect
    LaunchedEffect(snackbarViewModel) {
        launch {
            snackbarViewModel.snackbarMessage.collect { message ->
                if (message != null) {
                    snackbarHostState.showSnackbar(message)
                    Log.d("AppCanvas", "AppCanvas: show snack bar message: $message")
                    snackbarViewModel.clearSnackbar()
                }
            }
        }
    }

    // TODO: make snack bar work
    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Shopping App Demo by Lydia Yuan")
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Log.d("AppCanvas", "AppCanvas: padding: $it")
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("products") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    navigateToRegister = {
                        navController.navigate("register")
                    },
                    showSnackBarMessage = { message ->
                        snackbarViewModel.showSnackbar(message)
                    }
                )
            }
            composable("register") {
                RegisterScreen(
                    onLoginSuccess = {
                        navController.navigate("products") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    navigateToLogin = {
                        navController.navigate("login")
                    },
                    showSnackBarMessage = { message ->
                        snackbarViewModel.showSnackbar(message)
                    }
                )
            }
            composable("products") {
                ProductsScreen()
            }
        }
    }
}
