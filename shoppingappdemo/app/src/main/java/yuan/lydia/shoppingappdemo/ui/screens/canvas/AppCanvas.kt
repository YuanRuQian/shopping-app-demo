package yuan.lydia.shoppingappdemo.ui.screens.canvas

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import yuan.lydia.shoppingappdemo.data.cart.CartManagementViewModel
import yuan.lydia.shoppingappdemo.data.history.HistoryViewModel
import yuan.lydia.shoppingappdemo.data.shopping.ShoppingViewModel
import yuan.lydia.shoppingappdemo.data.utils.SnackbarViewModel
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.ui.screens.cart.CartScreen
import yuan.lydia.shoppingappdemo.ui.screens.history.HistoryScreen
import yuan.lydia.shoppingappdemo.ui.screens.history.OrderDetailsScreen
import yuan.lydia.shoppingappdemo.ui.screens.shopping.ShoppingScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.LoginScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.RegisterScreen
import yuan.lydia.shoppingappdemo.ui.screens.whishlist.WishlistScreen

// TODO: probably not pass live data to screens, hoist the live data to app canvas and pass the immutable data to screens
@Composable
fun AppCanvas(
    snackbarViewModel: SnackbarViewModel = viewModel(factory = SnackbarViewModel.Factory),
    cartManagementViewModel: CartManagementViewModel = viewModel(
        factory = CartManagementViewModel.Factory
    ),
    shoppingViewModel: ShoppingViewModel = viewModel(factory = ShoppingViewModel.Factory)
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
            AppTopBar(
                navController = navController,
                isUserLoggedIn = isUserLoggedIn,
                showSnackbarMessage = snackbarViewModel::showSnackbar,
                setIsUserLoggedIn = setIsUserLoggedIn
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, isUserLoggedIn)
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
                        showSnackBarMessage =
                        snackbarViewModel::showSnackbar
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
                        showSnackBarMessage = snackbarViewModel::showSnackbar
                    )
                }
                composable(AppRoute.Shopping.route) {
                    ShoppingScreen(
                        getFilteredProducts = { filter, maxPrice ->
                            shoppingViewModel.getFilteredProducts(filter, maxPrice)
                        },
                        getProducts = {
                            shoppingViewModel.getProducts(it)
                        },
                        productsLiveData = shoppingViewModel.filteredProducts,
                        increaseQuantity = cartManagementViewModel::increaseQuantityThenReloadUserCartData,
                        showSnackbarMessage = snackbarViewModel::showSnackbar,
                        addToWishList = cartManagementViewModel::addToWishlistAndReloadWishlistData,
                        removeFromWishList = cartManagementViewModel::removeFromWishlistAndReloadWishlistData,
                        loadWishList = cartManagementViewModel::loadWishlistData,
                        wishListLiveData = cartManagementViewModel.userWishlistLiveData
                    )
                }

                composable(AppRoute.Cart.route) {
                    CartScreen(
                        loadProductsData = { token ->
                            shoppingViewModel.getProducts(token)
                        },
                        productsLiveData = shoppingViewModel.filteredProducts,
                        loadUserCartData = cartManagementViewModel::loadUserCartData,
                        userCartDataLiveData = cartManagementViewModel.userCartLiveData,
                        updateQuantity = cartManagementViewModel::updateQuantityThenReloadUserCartData,
                        showSnackbarMessage = snackbarViewModel::showSnackbar,
                        checkout = cartManagementViewModel::checkout,
                        checkoutSuccessLiveData = cartManagementViewModel.checkoutSuccess,
                        onCheckoutSuccess = { username ->
                            cartManagementViewModel.clearUserCartAndReloadUserCartData(
                                username
                            )
                            navController.navigate(AppRoute.Shopping.route) {
                                popUpTo(AppRoute.Cart.route) { inclusive = true }
                            }
                            snackbarViewModel.showSnackbar("Order submitted successfully, $username!")
                        }
                    )
                }

                composable(AppRoute.History.route) {
                    HistoryScreen(
                        orderHistoryLiveData = historyViewModel.orderHistory,
                        getOrderHistory = historyViewModel::getOrderHistory,
                        checkOutOrderDetails = { orderId ->
                            navController.navigate("${AppRoute.OrderDetails.route}/${orderId}")
                        }
                    )
                }

                composable(AppRoute.Wishlist.route) {
                    WishlistScreen(
                        loadWishlistData = cartManagementViewModel::loadWishlistData,
                        wishlistLiveData = cartManagementViewModel.userWishlistLiveData,
                        loadProductsData = shoppingViewModel::getProducts,
                        productsLiveData = shoppingViewModel.filteredProducts,
                        removeFromWishList = cartManagementViewModel::removeFromWishlistAndReloadWishlistData,
                        showSnackbarMessage = snackbarViewModel::showSnackbar,
                        addToCart = cartManagementViewModel::addToCart
                    )
                }

                composable("${AppRoute.OrderDetails.route}/{orderId}") { backStackEntry ->
                    val orderId = backStackEntry.arguments?.getString("orderId")
                    if (orderId != null) {
                        OrderDetailsScreen(
                            getOrderDetail = { token ->
                                historyViewModel.getOrderDetails(token, orderId)
                            },
                            orderDetail = historyViewModel.currentOrderDetails.value,
                            onBack = historyViewModel::clearCurrentOrderDetails
                        )
                    } else {
                        snackbarViewModel.showSnackbar("No order id found")
                    }
                }
            }
        }
    }
}
