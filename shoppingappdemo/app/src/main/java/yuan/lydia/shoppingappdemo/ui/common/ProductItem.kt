package yuan.lydia.shoppingappdemo.ui.common

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.common.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(
    product: Product,
    increaseQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    addToWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    ifProductIsInWishlist: (Long) -> Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val (selectedQuantity, setSelectedQuantity) = remember { mutableIntStateOf(1) }

    val context = LocalContext.current
    val token = UserInfoManager.getInstance(context).getToken() ?: return
    val username = UserInfoManager.getInstance(context).getUsername() ?: return

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.6f) // Fluid left column
                        .padding(16.dp)
                ) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                    Text(
                        text = product.description,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )

                    Text(
                        text = "Price: $${product.retailPrice}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .clickable {
                                expanded = !expanded
                                Log.d("ProductItem", "ProductItem: clicked")
                            }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }) {
                            CompositionLocalProvider(LocalTextInputService provides null) {
                                Row(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    TextField(
                                        readOnly = true,
                                        value = selectedQuantity.toString(),
                                        onValueChange = {},
                                        prefix = { Text("Quantity:") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },
                                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                        modifier = Modifier
                                            .padding(
                                                top = 8.dp,
                                                end = 8.dp,
                                                bottom = 8.dp
                                            ) // Adjust padding as needed
                                            .menuAnchor()
                                    )


                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                    ) {
                                        for (quantity in 1..10) run {
                                            DropdownMenuItem(text = {
                                                Text(
                                                    text = quantity.toString(),
                                                )
                                            }, onClick = {
                                                setSelectedQuantity(quantity)
                                                expanded = false
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .padding(horizontal = 16.dp)
                ) {
                    WishlistButton(
                        productName = product.name,
                        isInWishlist = ifProductIsInWishlist(product.id),
                        addToWishList = addToWishList,
                        removeFromWishList = removeFromWishList,
                        productId = product.id,
                        showSnackbarMessage = showSnackbarMessage,
                        token = token
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AddToCartButton(
                        addToCart = increaseQuantity,
                        product = product,
                        showSnackbarMessage = showSnackbarMessage,
                        setSelectedQuantity = setSelectedQuantity,
                        selectedQuantity = selectedQuantity,
                        username = username
                    )
                }
            }
        }
    }
}