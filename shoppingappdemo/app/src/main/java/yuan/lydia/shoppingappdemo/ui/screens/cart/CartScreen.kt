package yuan.lydia.shoppingappdemo.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import yuan.lydia.shoppingappdemo.data.cart.database.CartItemEntity
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.shopping.Product
import yuan.lydia.shoppingappdemo.ui.common.PlaceholderScreen

@Composable
fun CartScreen(
    loadProductsData: (String) -> Unit,
    productsData: List<Product>?,
    loadUserCartData: (String) -> Unit,
    userCartData: List<CartItemEntity>?,
    updateQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    checkout: (String, List<CartItemEntity>) -> Unit,
    checkoutSuccessStatus: Boolean?,
    onCheckoutSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val userInfoManager = UserInfoManager.getInstance(context)
    val username = userInfoManager.getUsername()
    val token = userInfoManager.getToken()


    if (username == null || token == null) {
        PlaceholderScreen("Please login to view cart!")
        return
    }

    if (checkoutSuccessStatus == true) {
        onCheckoutSuccess(username)
    } else if (checkoutSuccessStatus == false) {
        showSnackbarMessage("Some of the products in your cart are out of stock, please remove them and try again")
    }

    LaunchedEffect(key1 = true) {
        loadProductsData(token)
        loadUserCartData(username)
    }

    fun getProductDataByProductId(productId: Long): Product? {
        return productsData?.find { it.id == productId }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (userCartData.isNullOrEmpty()) {
            PlaceholderScreen(info = "Your cart is empty!")
            return
        }

        UserCart(
            userCartData = userCartData,
            updateQuantity = { username, productId, newQuantity ->
                updateQuantity(username, productId, newQuantity)
            },
            showSnackbarMessage = showSnackbarMessage,
            getProductDataByProductId = ::getProductDataByProductId,
            token = token,
            checkout = checkout
        )
    }
}


@Composable
fun UserCart(
    userCartData: List<CartItemEntity>,
    updateQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    getProductDataByProductId: (Long) -> Product?,
    token: String,
    checkout: (String, List<CartItemEntity>) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(userCartData.size) { index ->
                CartItem(
                    cartItem = userCartData[index],
                    updateQuantity = updateQuantity,
                    showSnackbarMessage = showSnackbarMessage,
                    getProductDataByProductId = getProductDataByProductId,
                    useLastCardStyle = index == userCartData.size - 1
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Total: ${userCartData.sumOf { it.quantity }} items, $${
                        userCartData.sumOf {
                            it.quantity * (getProductDataByProductId(
                                it.productId
                            )?.retailPrice?.toInt() ?: 0)
                        }
                    }",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    checkout(token, userCartData)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Checkout", modifier = Modifier.padding(end = 8.dp))
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = "Checkout"
                    )
                }
            }
        }

    }
}


