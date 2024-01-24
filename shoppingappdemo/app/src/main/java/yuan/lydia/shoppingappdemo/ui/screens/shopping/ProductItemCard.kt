package yuan.lydia.shoppingappdemo.ui.screens.shopping

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import yuan.lydia.shoppingappdemo.ui.common.AddToCartButton
import yuan.lydia.shoppingappdemo.ui.common.WishlistButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItemCard(
    product: Product,
    increaseQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    addToWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    ifProductIsInWishlist: (Long) -> Boolean,
) {
    var expanded by remember { mutableStateOf(false) }
    val (selectedQuantity, setSelectedQuantity) = remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val token = UserInfoManager.getInstance(context).getToken() ?: return
    val username = UserInfoManager.getInstance(context).getUsername() ?: return

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = product.name,
                modifier = Modifier.padding(start = 4.dp),
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )
            Text(
                text = product.description,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                color = MaterialTheme.colorScheme.secondary,
            )

            Text(
                text = "Retail Price: $${product.retailPrice}",
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
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
                    onExpandedChange = {
                        expanded = it
                    }) {
                    CompositionLocalProvider(LocalTextInputService provides null) {
                        Row(
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            TextField(
                                readOnly = true,
                                value = if (selectedQuantity > 0) selectedQuantity.toString() else "",
                                onValueChange = {},
                                prefix = { if (selectedQuantity > 0) Text("Quantity:") else Text("To Add to Cart, Pick a Quantity") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier
                                    .padding(
                                        top = 8.dp,
                                        end = 8.dp
                                    )
                                    .width(if(selectedQuantity > 0)158.dp else 300.dp)
                                    .menuAnchor()
                            )



                            if (selectedQuantity > 0) {
                                IconButton(onClick = {
                                    setSelectedQuantity(0)
                                    expanded = false
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Restore,
                                        contentDescription = null
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Total Price: $${product.retailPrice * selectedQuantity}",
                                )
                            }

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

        val sharedModifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .height(IntrinsicSize.Min)


        @Composable
        fun renderAddToCartButton() {
            AddToCartButton(
                addToCart = increaseQuantity,
                product = product,
                showSnackbarMessage = showSnackbarMessage,
                setSelectedQuantity = setSelectedQuantity,
                selectedQuantity = selectedQuantity,
                username = username,
                modifier = sharedModifier.weight(5f),
                isOutlinedButton = false,
                enabled = selectedQuantity > 0
            )
        }

        @Composable
        fun renderWishlistButton() {
            WishlistButton(
                productName = product.name,
                isInWishlist = ifProductIsInWishlist(product.id),
                addToWishList = addToWishList,
                removeFromWishList = removeFromWishList,
                productId = product.id,
                showSnackbarMessage = showSnackbarMessage,
                token = token,
                modifier = sharedModifier.weight(7f),
                isOutlinedButton = true
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            renderAddToCartButton()
            Spacer(modifier = Modifier.height(8.dp))
            renderWishlistButton()
        }
    }
}