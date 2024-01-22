package yuan.lydia.shoppingappdemo.ui.screens.cart

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.CartItemEntity
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.shopping.Product


@Composable
fun CartScreen(
    loadProductsData: (String) -> Unit,
    productsLiveData: LiveData<List<Product>>,
    loadUserCartData: (String) -> Unit,
    userCartDataLiveData: LiveData<List<CartItemEntity>>,
    updateQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    checkout: (String, List<CartItemEntity>) -> Unit,
    onCheckoutSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val userInfoManager = UserInfoManager.getInstance(context)
    val username = userInfoManager.getUsername()
    val token = userInfoManager.getToken()
    val userCartData by userCartDataLiveData.observeAsState()
    val productsData by productsLiveData.observeAsState()

    if (username == null || token == null) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please login to view your cart",
                    fontFamily = MaterialTheme.typography.titleLarge.fontFamily
                )
            }
        }
        return
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$username's cart is empty!",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            return
        }

        UserCart(
            userCartData = userCartData!!,
            updateQuantity = { username, productId, newQuantity ->
                updateQuantity(username, productId, newQuantity)
            },
            showSnackbarMessage = showSnackbarMessage,
            getProductDataByProductId = ::getProductDataByProductId,
            token = token,
            username = username,
            checkout = checkout,
            onCheckoutSuccess = onCheckoutSuccess
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
    username: String,
    checkout: (String, List<CartItemEntity>) -> Unit,
    onCheckoutSuccess: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            items(userCartData.size) { index ->
                CartItem(
                    cartItem = userCartData[index],
                    updateQuantity = updateQuantity,
                    showSnackbarMessage = showSnackbarMessage,
                    getProductDataByProductId = getProductDataByProductId
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
            Text(
                text = "Total: ${userCartData.sumOf { it.quantity }} items, $${
                    userCartData.sumOf {
                        it.quantity * (getProductDataByProductId(
                            it.productId
                        )?.retailPrice?.toInt() ?: 0)
                    }
                }",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = {
                    checkout(token, userCartData)
                    onCheckoutSuccess(username)
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Row {
                    Text(text = "Checkout")
                    Icon(
                        imageVector = Icons.Default.ShoppingCartCheckout,
                        contentDescription = "Checkout"
                    )
                }
            }
        }

    }
}

@Composable
fun CartItem(
    cartItem: CartItemEntity,
    updateQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    getProductDataByProductId: (Long) -> Product?
) {

    Log.d("CartItem", "CartItem ${cartItem.productId} quantity: ${cartItem.quantity}}")

    val productInfo = getProductDataByProductId(cartItem.productId)

    if (productInfo == null) {
        Text(text = "Oops, product ${cartItem.productId} not found")
        return
    }

    val context = LocalContext.current
    val username = UserInfoManager.getInstance(context).getUsername()!!

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.518f)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = productInfo.name,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )

                    Text(
                        text = "Retail Price: $${productInfo.retailPrice}",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )

                    Text(text = "Quantity: ", modifier = Modifier.padding(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(onClick = {
                                // Decrease quantity
                                val selectedQuantity = maxOf(cartItem.quantity - 1, 0)
                                updateQuantity(username, cartItem.productId, selectedQuantity)
                                if (selectedQuantity > 0) {
                                    showSnackbarMessage("Remove one ${productInfo.name} from cart")
                                } else {
                                    showSnackbarMessage("Remove ${productInfo.name} from cart")
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease quantity"
                                )
                            }
                            OutlinedTextField(
                                readOnly = true,
                                value = cartItem.quantity.toString(),
                                onValueChange = {
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(50.dp)
                            )
                            IconButton(onClick = {
                                // Increase quantity
                                updateQuantity(username, cartItem.productId, cartItem.quantity + 1)
                                showSnackbarMessage("Add one more ${productInfo.name} to cart")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase quantity"
                                )
                            }
                        }
                    }

                    Text(
                        text = "Total: $${productInfo.retailPrice * cartItem.quantity}",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.482f)
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = {
                            updateQuantity(username, cartItem.productId, 0)
                            showSnackbarMessage("Remove ${productInfo.name} from cart")
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .height(IntrinsicSize.Min) // Optional: Ensure the button height is not too tall
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) // Adjust the corner radius as needed
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth() // Center the entire column horizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Increase quantity"
                            )
                            Text(
                                text = "Remove\nfrom\nCart",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

