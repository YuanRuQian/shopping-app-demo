package yuan.lydia.shoppingappdemo.ui.screens.cart

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.CartItemEntity
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager

@Composable
fun CartScreen(
    loadUserCartData: (String) -> Unit,
    userCartDataLiveData: LiveData<List<CartItemEntity>>,
    updateQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val username = UserInfoManager.getInstance(context).getUsername()
    val userCartData by userCartDataLiveData.observeAsState()

    if (username == null) {
        Text(text = "Please login to view your cart")
        return
    }

    LaunchedEffect(key1 = true) {
        loadUserCartData(username)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (userCartData.isNullOrEmpty()) {
            Text(text = "$username's cart is empty!")
            return
        }

        UserCart(
            userCartData = userCartData!!,
            updateQuantity = { username, productId, newQuantity ->
                updateQuantity(username, productId, newQuantity)
            },
            showSnackbarMessage = showSnackbarMessage
        )
    }
}

@Composable
fun UserCart(
    userCartData: List<CartItemEntity>,
    updateQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit
) {
    LazyColumn {
        items(userCartData.size) { index ->
            CartItem(
                cartItem = userCartData[index],
                updateQuantity = updateQuantity,
                showSnackbarMessage = showSnackbarMessage
            )
        }
    }
}

@Composable
fun CartItem(
    cartItem: CartItemEntity,
    updateQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
) {

    Log.d("CartItem", "CartItem ${cartItem.productId} quantity: ${cartItem.quantity}}")

    val context = LocalContext.current
    val username = UserInfoManager.getInstance(context).getUsername()!!

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Product ID: ${cartItem.productId}",
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )


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
                        updateQuantity(username, cartItem.productId, cartItem.quantity + 1)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase quantity"
                        )
                    }
                }

                IconButton(onClick = {
                    // Remove item
                    updateQuantity(username, cartItem.productId, 0)
                    showSnackbarMessage("Product ${cartItem.productId} removed from the cart")
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove item from cart"
                    )
                }
            }
        }
    }
}

