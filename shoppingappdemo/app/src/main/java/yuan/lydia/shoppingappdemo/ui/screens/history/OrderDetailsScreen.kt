package yuan.lydia.shoppingappdemo.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
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
import yuan.lydia.shoppingappdemo.data.utils.TokenManager
import yuan.lydia.shoppingappdemo.network.history.OrderDetail

@Composable
fun OrderDetailsScreen(getOrderDetail: (String) -> Unit, orderDetail: OrderDetail?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        val token = TokenManager.getInstance(context).getToken()!!

        LaunchedEffect(key1 = true) {
            getOrderDetail(token)
        }

        if (orderDetail == null) {
            Text(text = "Order id is null, no order details found")
            return
        }
        Text(text = orderDetail.toString())
    }
}