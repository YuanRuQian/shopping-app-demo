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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager

@Composable
fun WishlistButton(
    productName: String,
    isInWishlist: Boolean,
    addToWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    productId: Long,
    showSnackbarMessage: (String) -> Unit
) {
    val context = LocalContext.current
    val token = UserInfoManager.getInstance(context).getToken() ?: return
    Button(
        onClick = {
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
                imageVector = if (isInWishlist) Icons.Default.HeartBroken else Icons.Default.Favorite,
                contentDescription = null, // Content description can be null if the icon is decorative
                modifier = Modifier.size(24.dp) // Optional: Adjust size as needed
            )
            Text(
                text = if (isInWishlist) "Remove from Wishlist" else "Add to Wishlist",
                textAlign = TextAlign.Center
            )
        }
    }
}