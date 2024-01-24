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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import yuan.lydia.shoppingappdemo.data.cart.CartViewModel
import yuan.lydia.shoppingappdemo.data.history.HistoryViewModel
import yuan.lydia.shoppingappdemo.data.shopping.ShoppingViewModel
import yuan.lydia.shoppingappdemo.data.utils.SnackbarViewModel
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.data.wishlist.WishlistViewModel
import yuan.lydia.shoppingappdemo.ui.screens.cart.CartScreen
import yuan.lydia.shoppingappdemo.ui.screens.history.HistoryScreen
import yuan.lydia.shoppingappdemo.ui.screens.history.OrderDetailsScreen
import yuan.lydia.shoppingappdemo.ui.screens.shopping.ShoppingScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.LoginScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.RegisterScreen
import yuan.lydia.shoppingappdemo.ui.screens.whishlist.WishlistScreen

@Composable
fun AppCanvas(
    snackbarViewModel: SnackbarViewModel = viewModel(factory = SnackbarViewModel.Factory),
    cartViewModel: CartViewModel = viewModel(
        factory = CartViewModel.Factory
    ),
    shoppingViewModel: ShoppingViewModel = viewModel(factory = ShoppingViewModel.Factory),
    wishlistViewModel: WishlistViewModel = viewModel(factory = WishlistViewModel.Factory)
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val (isUserLoggedIn, setIsUserLoggedIn) = remember { mutableStateOf(false) }
    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
    val products by shoppingViewModel.filteredProducts.observeAsState()
    val wishlist by wishlistViewModel.wishlist.observeAsState()
    val userCart by cartViewModel.userCartLiveData.observeAsState()
    val orderHistory by historyViewModel.orderHistory.observeAsState()
    val currentOrderDetails by historyViewModel.currentOrderDetails.observeAsState()
    val( currentRoute, setCurrentRoute) = remember { mutableStateOf(AppRoute.Shopping.route) }

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
            BottomNavigationBar(navController, isUserLoggedIn, currentRoute, setCurrentRoute)
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
                                setCurrentRoute(AppRoute.Shopping.route)
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
                                setCurrentRoute(AppRoute.Shopping.route)
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
                        products = products,
                        addToCart = cartViewModel::increaseQuantityThenReloadUserCartData,
                        showSnackbarMessage = snackbarViewModel::showSnackbar,
                        addToWishList = wishlistViewModel::addToWishlistAndReloadWishlistData,
                        removeFromWishList = wishlistViewModel::removeFromWishlistAndReloadWishlistData,
                        loadWishList = wishlistViewModel::loadWishlist,
                        wishlist = wishlist,
                    )
                }

                composable(AppRoute.Cart.route) {
                    CartScreen(
                        loadProductsData = { token ->
                            shoppingViewModel.getProducts(token)
                        },
                        productsData = products,
                        loadUserCartData = cartViewModel::loadUserCartData,
                        userCartData = userCart,
                        updateQuantity = cartViewModel::updateQuantityThenReloadUserCartData,
                        showSnackbarMessage = snackbarViewModel::showSnackbar,
                        checkout = cartViewModel::checkout,
                        onCheckoutSuccess = { username ->
                            cartViewModel.clearUserCartAndReloadUserCartData(
                                username
                            )
                            navController.navigate(AppRoute.Shopping.route) {
                                popUpTo(AppRoute.Cart.route) { inclusive = true }
                                setCurrentRoute(AppRoute.Shopping.route)
                            }
                            snackbarViewModel.showSnackbar("Order submitted successfully, $username!")
                        }
                    )
                }

                composable(AppRoute.History.route) {
                    HistoryScreen(
                        orderHistory = orderHistory,
                        getOrderHistory = historyViewModel::getOrderHistory,
                        checkOutOrderDetails = { orderId ->
                            navController.navigate("${AppRoute.OrderDetails.route}/${orderId}")
                        }
                    )
                }

                composable(AppRoute.Wishlist.route) {
                    WishlistScreen(
                        loadWishlistData = wishlistViewModel::loadWishlist,
                        wishlist = wishlist,
                        removeFromWishList = wishlistViewModel::removeFromWishlistAndReloadWishlistData,
                        showSnackbarMessage = snackbarViewModel::showSnackbar,
                        addToCart = cartViewModel::increaseQuantity
                    )
                }

                composable("${AppRoute.OrderDetails.route}/{orderId}") { backStackEntry ->
                    val orderId = backStackEntry.arguments?.getString("orderId")
                    if (orderId != null) {
                        OrderDetailsScreen(
                            getOrderDetail = { token ->
                                historyViewModel.getOrderDetails(token, orderId)
                            },
                            orderDetail = currentOrderDetails,
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
