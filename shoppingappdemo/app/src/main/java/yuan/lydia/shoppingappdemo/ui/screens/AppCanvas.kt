package yuan.lydia.shoppingappdemo.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import yuan.lydia.shoppingappdemo.data.utils.SnackbarViewModel
import yuan.lydia.shoppingappdemo.ui.screens.shopping.ProductsScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.LoginScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.RegisterScreen

@Composable
fun AppCanvas() {
    val snackbarHostState = SnackbarHostState()
    val snackbarViewModel: SnackbarViewModel = viewModel(factory = SnackbarViewModel.Factory)
    val navController = rememberNavController()

    // Observe changes in the snackbarMessage using LaunchedEffect
    LaunchedEffect(snackbarViewModel) {
        launch {
            snackbarViewModel.snackbarMessage.collect { message ->
                if (message != null) {
                    snackbarHostState.showSnackbar(message)
                    Log.d("AppCanvas", "AppCanvas: show snack bar message: $message")
                    // snackbarViewModel.clearSnackbar()
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = androidx.compose.ui.Modifier.fillMaxSize()
            )
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
                    }
                )
            }
            composable("products") {
                ProductsScreen()
            }
        }
    }
}
