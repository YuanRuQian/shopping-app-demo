package yuan.lydia.shoppingappdemo.ui.screens.shopping

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import yuan.lydia.shoppingappdemo.data.shopping.ShoppingViewModel
import yuan.lydia.shoppingappdemo.data.utils.TokenManager
import yuan.lydia.shoppingappdemo.network.shopping.Product

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
        val token = TokenManager.getInstance(context).getToken()!!
        val products = shoppingViewModel.products.observeAsState()

        LaunchedEffect(key1 = true) {
            shoppingViewModel.getProducts(token)
        }

        ProductsList(products = products.value ?: emptyList())
    }
}


@Composable
fun ProductsList(products: List<Product>) {
    LazyColumn {
        products.forEach { product ->
            item {
                ProductItem(product = product)
            }
        }
    }
}

// TODO: add quantity change event
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(product: Product) {
    var expanded by remember { mutableStateOf(false) }
    var selectedQuantity by remember { mutableIntStateOf(1) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f) // Fluid left column
                        .padding(16.dp)
                ) {
                    Text(text = product.name, fontWeight = FontWeight.Bold)
                    Text(
                        text = product.description,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    // TODO: use localized price
                    Text(
                        text = "Price: $${product.retailPrice}",
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .clickable {
                                expanded = !expanded
                                Log.d("ProductItem", "ProductItem: clicked")
                            }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = it }) {
                            CompositionLocalProvider(LocalTextInputService provides null) {
                                Row(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    TextField(
                                        readOnly = true,
                                        value = selectedQuantity.toString(),
                                        onValueChange = {},
                                        prefix = { Text("Quantity:") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expanded
                                            )
                                        },
                                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                        modifier = Modifier
                                            .padding(
                                                start = 8.dp,
                                                top = 8.dp,
                                                end = 8.dp,
                                                bottom = 8.dp
                                            ) // Adjust padding as needed
                                            .menuAnchor()
                                    )


                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                    ) {
                                        for (quantity in 1..10) run {
                                            DropdownMenuItem(text = {
                                                Text(
                                                    text = quantity.toString(),
                                                )
                                            }, onClick = {
                                                selectedQuantity = quantity
                                                expanded = false
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                // Right column with Add to Cart button
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = {
                            // Add logic to add the product to the cart with the selected quantity
                            // You can pass the product and quantity to a function to handle cart addition
                            // TODO: handleAddToCart(product, selectedQuantity)
                        }
                    ) {
                        Text(text = "Add to Cart")
                    }
                }
            }
        }
    }
}
