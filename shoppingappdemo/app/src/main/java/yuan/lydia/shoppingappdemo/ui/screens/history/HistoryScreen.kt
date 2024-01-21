package yuan.lydia.shoppingappdemo.ui.screens.history

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import yuan.lydia.shoppingappdemo.data.utils.TokenManager
import yuan.lydia.shoppingappdemo.network.history.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun HistoryScreen(orderHistory: List<Order>?, getOrderHistory: (String) -> Unit) {
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
            getOrderHistory(token)
        }

        OrderHistoryList(orderHistory = orderHistory ?: emptyList())
    }
}


@Composable
fun OrderHistoryList(orderHistory: List<Order>) {
    if (orderHistory.isEmpty()) {
        Text(text = "No order history")
        return
    }
    LazyColumn {
        orderHistory.forEach { order ->
            item {
                OrderItem(order = order, onItemClick = { /*TODO*/ })
            }
        }
    }
}

// TODO: add quantity change event
@Composable
fun OrderItem(order: Order, onItemClick: (Order) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onItemClick(order) } // Click on the whole card to trigger navigation
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left column for text
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Text("Order ID: ${order.orderId}", fontWeight = FontWeight.Bold)
                Text("Status: ${order.orderStatus}")
                Text("Date Placed: ${formatDate(order.datePlaced)}", fontSize = 14.sp)
            }

            // Right column with icon button
            IconButton(
                onClick = { onItemClick(order) }, // Trigger navigation when the button is clicked
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = "Details")
            }
        }
    }
}


@SuppressLint("SimpleDateFormat")
fun formatDate(dateInMillis: Long): String {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US)
    val date = Date(dateInMillis)
    return dateFormat.format(date)
}
