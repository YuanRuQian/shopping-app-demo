package yuan.lydia.shoppingappdemo.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WishlistButton(
    productName: String,
    isInWishlist: Boolean,
    addToWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    productId: Long,
    showSnackbarMessage: (String) -> Unit,
    token: String,
    modifier: Modifier? = null,
    isOutlinedButton: Boolean = false
) {

    fun onClick() {
        if (isInWishlist) {
            removeFromWishList(
                token,
                productId,
                {
                    showSnackbarMessage("Removed $productName from wishlist!")
                },
                {
                    showSnackbarMessage(it)
                }
            )
        } else {
            addToWishList(
                token,
                productId,
                {
                    showSnackbarMessage("Added $productName to wishlist!")
                },
                {
                    showSnackbarMessage(it)
                }
            )
        }
    }

    if (isOutlinedButton) {
        OutlinedButton(
            onClick = { onClick() },
            modifier = modifier?: Modifier,
        ) {
            WishlistButtonContent(isInWishlist)
        }
    } else {
        Button(
            onClick = { onClick() },
            modifier = modifier?: Modifier,
        ) {
            WishlistButtonContent(isInWishlist)
        }
    }
}

@Composable
fun WishlistButtonContent(isInWishlist: Boolean) {
    Column(
        modifier = Modifier
            .testTag("wishlistButtonContent"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = if (isInWishlist) Icons.Default.HeartBroken else Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .testTag("wishlistButtonIcon")
        )
        Text(
            modifier = Modifier.testTag("wishlistButtonText"),
            text = if (isInWishlist) "Remove from Wishlist" else "Add to Wishlist",
            textAlign = TextAlign.Center
        )
    }
}