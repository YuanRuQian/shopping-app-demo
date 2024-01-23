package yuan.lydia.shoppingappdemo.ui.screens.shopping

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager
import yuan.lydia.shoppingappdemo.network.shopping.Product
import yuan.lydia.shoppingappdemo.ui.common.AddToCartButton
import yuan.lydia.shoppingappdemo.ui.common.PlaceholderScreen
import yuan.lydia.shoppingappdemo.ui.common.WishlistButton
import yuan.lydia.shoppingappdemo.network.wishlist.Product as WishlistProduct

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    getFilteredProducts: (filterType: FilterType, maxPrice: Int?) -> Unit,
    getProducts: (String) -> Unit,
    products: List<Product>?,
    addToCart: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    addToWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    loadWishList: (String) -> Unit,
    wishlist: List<WishlistProduct>?,
) {
    var maxPrice: Int? by remember { mutableStateOf(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedFilteredType by remember { mutableStateOf(FilterType.NONE) }

    val context = LocalContext.current
    val token = UserInfoManager.getInstance(context).getToken()
    val username = UserInfoManager.getInstance(context).getUsername()

    if (token == null || username == null) {
        PlaceholderScreen(info = "Please login to view products!")
        return
    }

    fun ifProductIsInWishlist(productId: Long): Boolean {
        if (wishlist.isNullOrEmpty()) {
            return false
        }

        return wishlist.find { it.id == productId } != null
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
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
                                        getFilteredProducts(
                                            filter,
                                            if (filter == FilterType.NO_MORE_THAN_MAX_PRICE) maxPrice else null
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
                            getFilteredProducts(
                                selectedFilteredType,
                                maxPrice
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


        LaunchedEffect(key1 = true) {
            getProducts(token)
            loadWishList(token)
        }

        ProductsList(
            products = products ?: emptyList(),
            increaseQuantity = addToCart,
            showSnackbarMessage = showSnackbarMessage,
            addToWishList = addToWishList,
            removeFromWishList = removeFromWishList,
            ifProductIsInWishlist = { ifProductIsInWishlist(it) }
        )
    }
}

@Composable
fun ProductsList(
    products: List<Product>,
    increaseQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    addToWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    ifProductIsInWishlist: (Long) -> Boolean
) {
    if (products.isEmpty()) {
        Text(text = "No products found")
    }

    LazyColumn {
        items(products) { product ->
            ProductItem(
                product = product,
                increaseQuantity = increaseQuantity,
                showSnackbarMessage = showSnackbarMessage,
                addToWishList = addToWishList,
                removeFromWishList = removeFromWishList,
                ifProductIsInWishlist = ifProductIsInWishlist
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(
    product: Product,
    increaseQuantity: (String, Long, Int) -> Unit,
    showSnackbarMessage: (String) -> Unit,
    addToWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    removeFromWishList: (String, Long, () -> Unit, (String) -> Unit) -> Unit,
    ifProductIsInWishlist: (Long) -> Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val (selectedQuantity, setSelectedQuantity) = remember { mutableIntStateOf(1) }

    val context = LocalContext.current
    val token = UserInfoManager.getInstance(context).getToken() ?: return
    val username = UserInfoManager.getInstance(context).getUsername() ?: return

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.6f) // Fluid left column
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
                                                setSelectedQuantity(quantity)
                                                expanded = false
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(0.4f)
                        .padding(horizontal = 16.dp)
                ) {
                    WishlistButton(
                        productName = product.name,
                        isInWishlist = ifProductIsInWishlist(product.id),
                        addToWishList = addToWishList,
                        removeFromWishList = removeFromWishList,
                        productId = product.id,
                        showSnackbarMessage = showSnackbarMessage,
                        token = token
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AddToCartButton(
                        addToCart = increaseQuantity,
                        product = product,
                        showSnackbarMessage = showSnackbarMessage,
                        setSelectedQuantity = setSelectedQuantity,
                        selectedQuantity = selectedQuantity,
                        username = username
                    )
                }
            }
        }
    }
}

