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
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.shopping.Product
import yuan.lydia.shoppingappdemo.ui.common.PlaceholderScreen

@Composable
fun WishlistScreen(
    loadWishlistData: (String) -> Unit,
    wishlistLiveData: LiveData<List<WishlistItemEntity>>,
    loadProductsData: (String) -> Unit,
    productsLiveData: LiveData<List<Product>>,
    removeFromWishList: (String, Long) -> Unit,
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
        val wishlist by wishlistLiveData.observeAsState()
        val productsData by productsLiveData.observeAsState()

        if (token == null || username == null) {
            PlaceholderScreen(info = "Please login to view your wishlist!")
            return
        }

        fun getProductDataByProductId(productId: Long): Product? {
            return productsData?.find { it.id == productId }
        }

        LaunchedEffect(key1 = true) {
            loadProductsData(token)
            loadWishlistData(username)
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
    wishlist: List<WishlistItemEntity>,
    getProductDataByProductId: (Long) -> Product?,
    removeFromWishList: (String, Long) -> Unit,
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
                wishlistItemEntity = wishlist[index],
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
    wishlistItemEntity: WishlistItemEntity,
    getProductDataByProductId: (Long) -> Product?,
    removeFromWishList: (String, Long) -> Unit,
    addToCart: (String, Long) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    username: String
) {
    val productId = wishlistItemEntity.productId
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
                    productname = product.name,
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
                    username = username,
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
    removeFromWishList: (String, Long) -> Unit,
    username: String,
    productId: Long,
    showSnackbarMessage: (String) -> Unit
) {
    Button(
        onClick = {
            removeFromWishList(username, productId)
            showSnackbarMessage("Removed $productname from Wishlist!")
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
                imageVector = Icons.Default.HeartBroken,
                contentDescription = null, // Content description can be null if the icon is decorative
                modifier = Modifier.size(24.dp) // Optional: Adjust size as needed
            )
            Text(
                text = "Remove from Wishlist",
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun AddToCartButton(
    productname: String,
    addToCart: (String, Long) -> Unit,
    username: String,
    productId: Long,
    showSnackbarMessage: (String) -> Unit
) {
    Button(
        onClick = {
            addToCart(username, productId)
            showSnackbarMessage("Add $productname to Cart!")
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