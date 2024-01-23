package yuan.lydia.shoppingappdemo.ui.screens.cart

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import yuan.lydia.shoppingappdemo.network.shopping.Product

@Composable
fun CartItem(
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
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Retail Price: $${productInfo.retailPrice}",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    )

                    Text(text = "Quantity: ", modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
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
