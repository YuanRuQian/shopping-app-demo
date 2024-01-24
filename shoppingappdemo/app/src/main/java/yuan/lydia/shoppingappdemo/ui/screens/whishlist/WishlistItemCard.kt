package yuan.lydia.shoppingappdemo.ui.screens.whishlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.common.Product
import yuan.lydia.shoppingappdemo.ui.common.AddToCartButton
import yuan.lydia.shoppingappdemo.ui.common.WishlistButton

@Composable
fun WishlistItemCard(
    product: Product,
    increaseQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    ifProductIsInWishlist: (Long) -> Boolean
) {
    val (selectedQuantity, setSelectedQuantity) = remember { mutableIntStateOf(1) }

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
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )
            Text(
                text = product.description,
                modifier = Modifier.padding(top = 4.dp),
                color = MaterialTheme.colorScheme.secondary
            )

            Text(
                text = "Price: $${product.retailPrice}",
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        val sharedModifier = Modifier
            .padding(8.dp)
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
                isOutlinedButton = true
            )
        }

        @Composable
        fun renderWishlistButton() {
            WishlistButton(
                productName = product.name,
                isInWishlist = ifProductIsInWishlist(product.id),
                addToWishList = { _, _, _, _ -> },
                removeFromWishList = removeFromWishList,
                productId = product.id,
                showSnackbarMessage = showSnackbarMessage,
                token = token,
                modifier = sharedModifier.weight(7f),
                isOutlinedButton = false
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
                renderWishlistButton()
                Spacer(modifier = Modifier.height(8.dp))
                renderAddToCartButton()
        }
    }
}