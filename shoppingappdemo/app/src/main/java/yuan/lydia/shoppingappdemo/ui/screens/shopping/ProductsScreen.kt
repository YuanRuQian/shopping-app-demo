package yuan.lydia.shoppingappdemo.ui.screens.shopping

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import yuan.lydia.shoppingappdemo.data.shopping.ShoppingViewModel
import yuan.lydia.shoppingappdemo.data.shopping.UiState
import yuan.lydia.shoppingappdemo.data.utils.TokenManager

@Composable
fun ProductsScreen(shoppingViewModel: ShoppingViewModel = viewModel(factory = ShoppingViewModel.Factory)) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        val token = TokenManager.getInstance(context).getToken()
        if (token == null) {
            Log.d("ProductsScreen", "No token!")
            return
        } else {
            Button(onClick = { shoppingViewModel.getProducts(token) }) {
                Text(text = "Get Products")
            }
        }

        when (val uiState = shoppingViewModel.uiState) {
            is UiState.Loading -> {
                Text(text = "Loading...")
            }
            is UiState.Uninitialized -> {
                Text(text = "Uninitialized")
            }
            is UiState.ProductError -> {
                Text(text = "ProductError")
            }
            is UiState.ProductNetworkError -> {
                Text(text = "ProductNetworkError")
            }
            is UiState.ProductsSuccess -> {
                Log.d("ProductsScreen", "ProductsSuccess: ${uiState.response}")
                Text(text = "ProductsSuccess")
            }
        }
    }
}