package yuan.lydia.shoppingappdemo.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.network.common.Product


@Composable
fun AddToCartButton(
    addToCart: (String, Long, Int) -> Unit,
    product: Product,
    showSnackbarMessage: (String) -> Unit,
    setSelectedQuantity: (Int) -> Unit,
    selectedQuantity: Int,
    username: String,
    modifier: Modifier,
    isOutlinedButton: Boolean = false,
) {

    fun onClick() {
        addToCart(
            username,
            product.id,
            selectedQuantity
        )
        showSnackbarMessage("Added $selectedQuantity ${product.name} to cart!")
        setSelectedQuantity(0)
    }

    if (isOutlinedButton) {
        OutlinedButton(
            enabled = selectedQuantity > 0,
            onClick = {
                onClick()
            },
            modifier = modifier
        ) {
            AddToCartButtonContent()
        }
    } else {
        Button(
            enabled = selectedQuantity > 0,
            onClick = {
                onClick()
            },
            modifier = modifier
        ) {
            AddToCartButtonContent()
        }
    }
}

@Composable
fun AddToCartButtonContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null, // Content description can be null if the icon is decorative
            modifier = Modifier.size(24.dp) // Optional: Adjust size as needed
        )
        Text(
            text = "Add to Cart",
            textAlign = TextAlign.Center
        )
    }
}
