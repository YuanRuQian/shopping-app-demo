package yuan.lydia.shoppingappdemo.ui.screens.cart

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.data.cart.database.CartItemEntity
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.common.Product

@Composable
fun CartItemCard(
    cartItem: CartItemEntity,
    updateQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    getProductDataByProductId: (Long) -> Product?,
    useLastCardStyle: Boolean
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
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = if (useLastCardStyle) 84.dp else 16.dp
            )
    ) {


        Text(
            text = productInfo.name,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Retail Price: $${productInfo.retailPrice}",
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )


        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Quantity: ",
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }

            Column {
                Row {
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
                        modifier = Modifier.width(56.dp),
                        readOnly = true,
                        value = cartItem.quantity.toString(),
                        onValueChange = {
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
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

        }

        Text(
            text = "Total: $${productInfo.retailPrice * cartItem.quantity}",
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
        )


        Button(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .fillMaxWidth(),
            onClick = {
                updateQuantity(username, cartItem.productId, 0)
                showSnackbarMessage("Remove ${productInfo.name} from cart")
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Remove from Cart",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Increase quantity"
                )
            }
        }
    }
}
