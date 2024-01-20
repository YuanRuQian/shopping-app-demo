package yuan.lydia.shoppingappdemo.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import yuan.lydia.shoppingappdemo.data.history.HistoryViewModel
import yuan.lydia.shoppingappdemo.data.utils.TokenManager
import yuan.lydia.shoppingappdemo.network.history.Order


@Composable
fun HistoryScreen(historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        val token = TokenManager.getInstance(context).getToken()!!
        val orderHistory = historyViewModel.orderHistory.observeAsState()

        LaunchedEffect(key1 = true) {
            historyViewModel.getOrderHistory(token)
        }

        OrderHistoryList(orderHistory = orderHistory.value ?: emptyList())
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
                OrderItem(order = order)
            }
        }
    }
}

// TODO: add quantity change event
@Composable
fun OrderItem(order: Order) {

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = order.toString())
    }
}