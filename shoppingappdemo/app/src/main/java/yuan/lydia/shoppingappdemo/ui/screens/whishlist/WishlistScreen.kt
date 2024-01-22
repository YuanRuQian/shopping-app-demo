package yuan.lydia.shoppingappdemo.ui.screens.whishlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.shopping.Product
import yuan.lydia.shoppingappdemo.ui.common.PlaceholderScreen
import yuan.lydia.shoppingappdemo.ui.common.WishlistButton
import yuan.lydia.shoppingappdemo.network.wishlist.Product as WishlistProduct

@Composable
fun WishlistScreen(
    loadWishlistData: (String) -> Unit,
    wishlist: List<WishlistProduct>?,
    productsData: List<Product>?,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    addToCart: (String, Long) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        val context = LocalContext.current
        val userInfoManager = UserInfoManager.getInstance(context)
        val token = userInfoManager.getToken()
        val username = userInfoManager.getUsername()

        if (token == null || username == null) {
            PlaceholderScreen(info = "Please login to view your wishlist!")
            return
        }

        fun getProductDataByProductId(productId: Long): Product? {
            return productsData?.find { it.id == productId }
        }

        LaunchedEffect(key1 = true) {
            loadWishlistData(token)
        }

        Wishlist(
            wishlist = wishlist ?: emptyList(),
            getProductDataByProductId = ::getProductDataByProductId,
            removeFromWishList = removeFromWishList,
            showSnackbarMessage = showSnackbarMessage,
            username = username,
            addToCart = addToCart
        )
    }
}

@Composable
fun Wishlist(
    wishlist: List<WishlistProduct>,
    getProductDataByProductId: (Long) -> Product?,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    username: String,
    addToCart: (String, Long) -> Unit
) {
    if (wishlist.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Your wishlist is empty",
                fontFamily = MaterialTheme.typography.titleLarge.fontFamily
            )
        }
        return
    }

    LazyColumn {
        items(wishlist.size) { index ->
            WishlistItem(
                wishlistProduct = wishlist[index],
                getProductDataByProductId = getProductDataByProductId,
                removeFromWishList = removeFromWishList,
                showSnackbarMessage = showSnackbarMessage,
                username = username,
                addToCart = addToCart
            )
        }
    }
}

@Composable
fun WishlistItem(
    wishlistProduct: WishlistProduct,
    getProductDataByProductId: (Long) -> Product?,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    addToCart: (String, Long) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    username: String
) {
    val productId = wishlistProduct.id
    val product = getProductDataByProductId(productId) ?: return

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Column
            Column(
                modifier = Modifier
                    .weight(0.6f) // 60% width
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )

                AddToCartButton(
                    productName = product.name,
                    addToCart = addToCart,
                    username = username,
                    productId = productId,
                    showSnackbarMessage = showSnackbarMessage
                )
            }

            // Right Column
            Column(
                modifier = Modifier
                    .weight(0.4f) // 40% width
            ) {
                RemoveWishlistButton(
                    productname = product.name,
                    removeFromWishList = removeFromWishList,
                    productId = productId,
                    showSnackbarMessage = showSnackbarMessage
                )
            }
        }
    }
}


@Composable
fun RemoveWishlistButton(
    productname: String,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    productId: Long,
    showSnackbarMessage: (String) -> Unit
) {
    WishlistButton(
        productName = productname,
        isInWishlist = true,
        addToWishList = { _, _, _, _ -> },
        removeFromWishList = removeFromWishList,
        productId = productId,
        showSnackbarMessage = showSnackbarMessage
    )
}


@Composable
fun AddToCartButton(
    productName: String,
    addToCart: (String, Long) -> Unit,
    username: String,
    productId: Long,
    showSnackbarMessage: (String) -> Unit
) {
    Button(
        onClick = {
            addToCart(username, productId)
            showSnackbarMessage("Add $productName to Cart!")
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