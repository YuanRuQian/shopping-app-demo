package yuan.lydia.shoppingappdemo.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import yuan.lydia.shoppingappdemo.data.history.HistoryViewModel
import yuan.lydia.shoppingappdemo.data.utils.SnackbarViewModel
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.ui.screens.cart.CartScreen
import yuan.lydia.shoppingappdemo.ui.screens.history.HistoryScreen
import yuan.lydia.shoppingappdemo.ui.screens.history.OrderDetailsScreen
import yuan.lydia.shoppingappdemo.ui.screens.shopping.ShoppingScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.LoginScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.RegisterScreen
import yuan.lydia.shoppingappdemo.ui.screens.whishlist.WishlistScreen

sealed class AppRoute(val route: String) {
    data object Login : AppRoute("Login")
    data object Register : AppRoute("Register")
    data object Shopping : AppRoute("Shopping")

    data object Cart : AppRoute("Cart")
    data object History : AppRoute("History")
    data object Wishlist : AppRoute("Wishlist")

    data object OrderDetails : AppRoute("OrderDetails")
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
    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)

    LaunchedEffect(key1 = true) {
        setIsUserLoggedIn(UserInfoManager.getInstance(context).isTokenExist())
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
                    if (!isUserLoggedIn) {
                        Text("Shopping App Demo by Lydia Yuan")
                    } else {
                        Text(
                            "Enjoy shopping, ${
                                UserInfoManager.getInstance(context).getUsername()
                            }!"
                        )
                    }
                },
                actions = {
                    ExitButton(
                        isUserLoggedIn,
                        context,
                        navController,
                        snackbarViewModel,
                        setIsUserLoggedIn
                    )
                },
            )
        },
        bottomBar = {
            if (isUserLoggedIn) {
                BottomNavigationBar(navController)
            }
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
                startDestination = if (isUserLoggedIn) AppRoute.Shopping.route else AppRoute.Login.route,
            ) {
                composable(AppRoute.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            setIsUserLoggedIn(true)
                            navController.navigate(AppRoute.Shopping.route) {
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
                            navController.navigate(AppRoute.Shopping.route) {
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
                composable(AppRoute.Shopping.route) {
                    ShoppingScreen()
                }

                composable(AppRoute.Cart.route) {
                    CartScreen()
                }

                composable(AppRoute.History.route) {
                    HistoryScreen(
                        orderHistory = historyViewModel.orderHistory.value,
                        getOrderHistory = { token ->
                            historyViewModel.getOrderHistory(token)
                        },
                        checkOutOrderDetails = { orderId ->
                            navController.navigate("${AppRoute.OrderDetails.route}/${orderId}")
                        }
                    )
                }

                composable(AppRoute.Wishlist.route) {
                    WishlistScreen()
                }

                composable("${AppRoute.OrderDetails.route}/{orderId}") { backStackEntry ->
                    val orderId = backStackEntry.arguments?.getString("orderId")
                    if (orderId != null) {
                        OrderDetailsScreen(
                            getOrderDetail = { token ->
                                historyViewModel.getOrderDetails(token, orderId)
                            },
                            orderDetail = historyViewModel.currentOrderDetails.value,
                            onBack = {
                                historyViewModel.clearCurrentOrderDetails()
                            }
                        )
                    } else {
                        snackbarViewModel.showSnackbar("No order id found")
                    }
                }

            }
        }
    }
}

fun logout(
    context: Context,
    navController: NavController,
    snackbarViewModel: SnackbarViewModel,
    setIsUserLoggedIn: (Boolean) -> Unit
) {
    val userInfoManager = UserInfoManager.getInstance(context)
    userInfoManager.clearToken()
    val currentUsername = userInfoManager.getUsername()
    userInfoManager.clearUsername()
    setIsUserLoggedIn(false)
    navController.navigate(AppRoute.Login.route) {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
    snackbarViewModel.showSnackbar("See you next time, $currentUsername!")
}

@Composable
fun BottomNavigationBarButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { onClick() }) {
            Icon(
                icon,
                contentDescription = text
            )
        }
        Text(text)
    }
}

fun navigationWithDestinationPreCheck(
    navController: NavController,
    destination: String,
    navBackStackEntry: NavBackStackEntry?
) {
    if (navBackStackEntry?.destination?.route != destination) {
        navController.navigate(destination)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationBarButton(
                icon = Icons.Filled.Search,
                text = AppRoute.Shopping.route,
                onClick = {
                    navigationWithDestinationPreCheck(
                        navController,
                        AppRoute.Shopping.route,
                        navBackStackEntry
                    )
                }
            )
            BottomNavigationBarButton(
                icon = Icons.Filled.ShoppingBasket,
                text = AppRoute.Cart.route,
                onClick = {
                    navigationWithDestinationPreCheck(
                        navController,
                        AppRoute.Cart.route,
                        navBackStackEntry
                    )
                }
            )
            BottomNavigationBarButton(
                icon = Icons.Filled.History,
                text = AppRoute.History.route,
                onClick = {
                    navigationWithDestinationPreCheck(
                        navController,
                        AppRoute.History.route,
                        navBackStackEntry
                    )
                }
            )
            BottomNavigationBarButton(
                icon = Icons.Filled.Favorite,
                text = AppRoute.Wishlist.route,
                onClick = {
                    navigationWithDestinationPreCheck(
                        navController,
                        AppRoute.Wishlist.route,
                        navBackStackEntry
                    )
                }
            )
        }
    }
}


@Composable
fun ExitButton(
    isUserLoggedIn: Boolean,
    context: Context,
    navController: NavController,
    snackbarViewModel: SnackbarViewModel,
    setIsUserLoggedIn: (Boolean) -> Unit
) {
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
}