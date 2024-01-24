package yuan.lydia.shoppingappdemo.ui.screens.whishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.common.Product
import yuan.lydia.shoppingappdemo.ui.common.PlaceholderScreen
import yuan.lydia.shoppingappdemo.ui.common.ProductItemCard

@Composable
fun WishlistScreen(
    loadWishlistData: (String) -> Unit,
    wishlist: List<Product>?,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    addToCart: (String, Long, Int) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize(),
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

        LaunchedEffect(key1 = true) {
            loadWishlistData(token)
        }

        Wishlist(
            wishlist = wishlist ?: emptyList(),
            removeFromWishList = removeFromWishList,
            showSnackbarMessage = showSnackbarMessage,
            addToCart = addToCart
        )
    }
}

@Composable
fun Wishlist(
    wishlist: List<Product>,
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
            ProductItemCard(
                product = wishlist[index],
                increaseQuantity = addToCart,
                showSnackbarMessage = showSnackbarMessage,
                addToWishList = { _, _, _, _ -> },
                removeFromWishList = removeFromWishList,
                ifProductIsInWishlist = { true },
                wishlistMode = true
            )
        }
    }
}