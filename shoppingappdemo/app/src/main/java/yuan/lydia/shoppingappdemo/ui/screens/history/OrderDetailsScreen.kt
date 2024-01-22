package yuan.lydia.shoppingappdemo.ui.screens.history

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.history.OrderDetail
import yuan.lydia.shoppingappdemo.network.history.OrderItem
import yuan.lydia.shoppingappdemo.ui.common.PlaceholderScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderDetailsScreen(
    getOrderDetail: (String) -> Unit,
    orderDetail: OrderDetail?,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val context = LocalContext.current
        val token = UserInfoManager.getInstance(context).getToken()

        if(token == null) {
            PlaceholderScreen(info = "Please login to view order details!")
            return
        }

        LaunchedEffect(key1 = true) {
            getOrderDetail(token)
        }

        val onBackPressedDispatcher =
            LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        var backPressHandled by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        BackHandler(enabled = !backPressHandled) {
            onBack()
            Log.d("OrderDetailsScreen", "BackHandler: $backPressHandled")
            backPressHandled = true
            coroutineScope.launch {
                awaitFrame()
                onBackPressedDispatcher?.onBackPressed()
                backPressHandled = false
            }
        }


        if (orderDetail == null) {
            PlaceholderScreen(info = "No order details found!")
        } else {

            // Display order items
            if (orderDetail.orderItems.isNotEmpty()) {
                LazyColumn {
                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(text = "Order ID: ${orderDetail.id}", fontWeight = FontWeight.Bold)
                            Text(text = "Order Status: ${orderDetail.orderStatus}")
                            Text(text = "Date Placed: ${formatDate(orderDetail.datePlaced)}")
                            Text(text = "Total: $${orderDetail.orderItems.sumOf { it.purchasedPrice * it.quantity }}")
                            Text(text = "You Saved: $${orderDetail.orderItems.sumOf { it.product.retailPrice * it.quantity - it.purchasedPrice * it.quantity }}")
                            Text(text = "Order Items:", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    items(orderDetail.orderItems.size) { index ->
                        OrderItemCard(orderItem = orderDetail.orderItems[index])
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "No order items found")
            }
        }
    }
}

@Composable
fun OrderItemCard(orderItem: OrderItem) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Product: ${orderItem.product.name}", fontWeight = FontWeight.Bold)
            Text("Description: ${orderItem.product.description}")
            Text("Retail Price: $${orderItem.product.retailPrice}")
            Text("Purchased Price: $${orderItem.purchasedPrice}")
            Text("Quantity: ${orderItem.quantity}")
            Text("Total: $${orderItem.purchasedPrice * orderItem.quantity}")
        }
    }
}