package yuan.lydia.shoppingappdemo.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.network.shopping.Product


@Composable
fun AddToCartButton(
    addToCart: (String, Long, Int) -> Unit,
    product: Product,
    showSnackbarMessage: (String) -> Unit,
    setSelectedQuantity: (Int) -> Unit,
    selectedQuantity: Int,
    username: String
) {
    Button(
        onClick = {
            addToCart(
                username,
                product.id,
                selectedQuantity
            )
            showSnackbarMessage("Added $selectedQuantity ${product.name} to cart!")
            setSelectedQuantity(1)
        },
        modifier = Modifier
            .padding(8.dp)
            .height(IntrinsicSize.Min)
            .background(
                MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth() // Center the entire column horizontally
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
}
