package yuan.lydia.shoppingappdemo.ui.screens.shopping

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import yuan.lydia.shoppingappdemo.data.shopping.ShoppingViewModel
import yuan.lydia.shoppingappdemo.data.utils.TokenManager
import yuan.lydia.shoppingappdemo.network.shopping.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(shoppingViewModel: ShoppingViewModel = viewModel(factory = ShoppingViewModel.Factory)) {
    var maxPrice: Int? by remember { mutableStateOf(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedFilteredType by remember { mutableStateOf(FilterType.NONE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Left column with Filter dropdown
            Box(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }) {
                    CompositionLocalProvider(LocalTextInputService provides null) {
                        Row(
                            modifier = Modifier
                                .menuAnchor(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            TextField(
                                readOnly = true,
                                value = selectedFilteredType.readableText,
                                onValueChange = {},
                                prefix = { Text("Filter:") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                for (filter in FilterType.entries) {
                                    DropdownMenuItem(text = {
                                        Text(
                                            text = filter.readableText,
                                        )
                                    }, onClick = {
                                        selectedFilteredType = filter
                                        shoppingViewModel.getFilteredProducts(
                                            filterType = filter,
                                            maxPrice = if (filter == FilterType.NO_MORE_THAN_MAX_PRICE) maxPrice else null
                                        )
                                        expanded = false
                                    })
                                }
                            }
                        }
                    }
                }
            }

            if (selectedFilteredType == FilterType.NO_MORE_THAN_MAX_PRICE) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                ) {
                    OutlinedTextField(
                        value = maxPrice?.toString() ?: "",
                        onValueChange = {
                            maxPrice = it.toIntOrNull()
                            shoppingViewModel.getFilteredProducts(
                                filterType = selectedFilteredType,
                                maxPrice = maxPrice
                            )
                        },
                        label = {
                            Text(
                                text = "Max Price",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        visualTransformation = if (maxPrice == null) VisualTransformation.None else VisualTransformation.None // Hide text when null
                    )

                }
            }
        }


        val context = LocalContext.current
        val token = TokenManager.getInstance(context).getToken()!!
        val products = shoppingViewModel.filteredProducts.observeAsState()

        LaunchedEffect(key1 = true) {
            shoppingViewModel.getProducts(token)
        }

        ProductsList(products = products.value ?: emptyList())
    }
}

@Composable
fun ProductsList(products: List<Product>) {
    if (products.isEmpty()) {
        Text(text = "No products found")
    }
    LazyColumn {
        items(products) { product ->
            ProductItem(product = product)
        }
    }
}

enum class FilterType(val readableText: String) {
    NONE("None"),
    LOW_TO_HIGH("Price Low to High"),
    HIGH_TO_LOW("Price High to Low"),
    ALPHABETICAL_ASC("Sort A-Z"),
    ALPHABETICAL_DESC("Sort Z-A"),
    NO_MORE_THAN_MAX_PRICE("Under Max Price")
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
                        .weight(0.618f) // Fluid left column
                        .padding(16.dp)
                ) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                    Text(
                        text = product.description,
                        modifier = Modifier.padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
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
                        .weight(0.382f)
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = {
                            // Add logic to add the product to the cart with the selected quantity
                            // You can pass the product and quantity to a function to handle cart addition
                            // TODO: handleAddToCart(product, selectedQuantity)
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .height(IntrinsicSize.Min) // Optional: Ensure the button height is not too tall
                            .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)) // Adjust the corner radius as needed
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
            }
        }
    }
}
