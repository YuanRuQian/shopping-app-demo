package yuan.lydia.shoppingappdemo.ui.screens.whishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.shopping.Product
import yuan.lydia.shoppingappdemo.ui.common.AddToCartButton
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
    addToCart: (String, Long, Int) -> Unit
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
    addToCart: (String, Long, Int) -> Unit
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
                addToCart = addToCart,
                showSnackbarMessage = showSnackbarMessage
            )
        }
    }
}

@Composable
fun WishlistItem(
    wishlistProduct: yuan.lydia.shoppingappdemo.network.wishlist.Product,
    getProductDataByProductId: (Long) -> Product?,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    addToCart: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit
) {
    val productId = wishlistProduct.id
    val product = getProductDataByProductId(productId) ?: return
    val context = LocalContext.current
    val username = UserInfoManager.getInstance(context).getUsername() ?: return

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
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )

                Text(
                    text = product.description,
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )

                AddToCartButton(
                    addToCart = addToCart,
                    product = product,
                    showSnackbarMessage = showSnackbarMessage,
                    setSelectedQuantity = {},
                    selectedQuantity = 1,
                    username = username
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
    val context = LocalContext.current
    val token = UserInfoManager.getInstance(context).getToken() ?: return
    WishlistButton(
        productName = productname,
        isInWishlist = true,
        addToWishList = { _, _, _, _ -> },
        removeFromWishList = removeFromWishList,
        productId = productId,
        showSnackbarMessage = showSnackbarMessage,
        token = token
    )
}