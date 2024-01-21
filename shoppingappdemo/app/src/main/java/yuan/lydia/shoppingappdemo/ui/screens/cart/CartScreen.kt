package yuan.lydia.shoppingappdemo.ui.screens.cart

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.data.cartWishlistManagement.entities.CartItemEntity
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager

@Composable
fun CartScreen(loadUserCartData: (String) -> Unit, userCartData: List<CartItemEntity>?) {
    val context = LocalContext.current
    val username = UserInfoManager.getInstance(context).getUsername()!!

    LaunchedEffect(key1 = true) {
        loadUserCartData(username)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (userCartData.isNullOrEmpty()) {
            Text(text = "$username's cart is empty!")
            return
        }

        UserCart(userCartData)
    }
}

@Composable
fun UserCart(userCartData: List<CartItemEntity>) {

    userCartData.forEach {
        Text(text = it.toString())
    }
}