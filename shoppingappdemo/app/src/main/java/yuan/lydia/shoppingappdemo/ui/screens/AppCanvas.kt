package yuan.lydia.shoppingappdemo.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import yuan.lydia.shoppingappdemo.data.utils.SnackbarViewModel
import yuan.lydia.shoppingappdemo.data.utils.TokenManager
import yuan.lydia.shoppingappdemo.ui.screens.shopping.ProductsScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.LoginScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.RegisterScreen

sealed class AppRoute(val route: String) {
    data object Login : AppRoute("login")
    data object Register : AppRoute("register")
    data object Products : AppRoute("products")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCanvas(
    snackbarViewModel: SnackbarViewModel = viewModel(factory = SnackbarViewModel.Factory)
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val (isUserLoggedIn, setIsUserLoggedIn) = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        setIsUserLoggedIn(TokenManager.getInstance(context).isTokenExist())
    }

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

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Shopping App Demo by Lydia Yuan")
                },
                actions = {
                    if (isUserLoggedIn) {
                        IconButton(onClick = {
                            logout(
                                context,
                                navController,
                                snackbarViewModel,
                                setIsUserLoggedIn
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ExitToApp,
                                contentDescription = "Log out and exit the app"
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NavHost(
                navController = navController,
                startDestination = if (isUserLoggedIn) AppRoute.Products.route else AppRoute.Login.route,
            ) {
                composable(AppRoute.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            setIsUserLoggedIn(true)
                            navController.navigate(AppRoute.Products.route) {
                                popUpTo(AppRoute.Login.route) { inclusive = true }
                            }
                        },
                        navigateToRegister = {
                            navController.navigate(AppRoute.Register.route)
                        },
                        showSnackBarMessage = { message ->
                            snackbarViewModel.showSnackbar(message)
                        }
                    )
                }
                composable(AppRoute.Register.route) {
                    RegisterScreen(
                        onLoginSuccess = {
                            setIsUserLoggedIn(true)
                            navController.navigate(AppRoute.Products.route) {
                                popUpTo(AppRoute.Register.route) { inclusive = true }
                            }
                        },
                        navigateToLogin = {
                            navController.navigate(AppRoute.Login.route)
                        },
                        showSnackBarMessage = { message ->
                            snackbarViewModel.showSnackbar(message)
                        }
                    )
                }
                composable(AppRoute.Products.route) {
                    ProductsScreen()
                }
            }
        }
    }
}

fun logout(
    context: Context,
    navController: androidx.navigation.NavController,
    snackbarViewModel: SnackbarViewModel,
    setIsUserLoggedIn: (Boolean) -> Unit
) {
    TokenManager.getInstance(context).clearToken()
    setIsUserLoggedIn(false)
    navController.navigate(AppRoute.Login.route) {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
    snackbarViewModel.showSnackbar("See you next time!")
}