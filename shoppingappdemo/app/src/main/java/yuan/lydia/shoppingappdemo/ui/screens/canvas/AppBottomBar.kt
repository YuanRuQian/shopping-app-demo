package yuan.lydia.shoppingappdemo.ui.screens.canvas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

fun navigationWithDestinationPreCheck(
    navController: NavController,
    destination: String,
    navBackStackEntry: NavBackStackEntry?
) {
    if (navBackStackEntry?.destination?.route != destination) {
        navController.navigate(destination)
    }
}

// TODO: highlight current screen button
@Composable
fun BottomNavigationBar(navController: NavController, isUserLoggedIn: Boolean) {
    if (!isUserLoggedIn) {
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavigationBarButton(
                icon = Icons.Filled.Search,
                text = AppRoute.Shopping.route,
                onClick = {
                    navigationWithDestinationPreCheck(
                        navController,
                        AppRoute.Shopping.route,
                        navBackStackEntry
                    )
                }
            )
            BottomNavigationBarButton(
                icon = Icons.Filled.ShoppingCart,
                text = AppRoute.Cart.route,
                onClick = {
                    navigationWithDestinationPreCheck(
                        navController,
                        AppRoute.Cart.route,
                        navBackStackEntry
                    )
                }
            )
            BottomNavigationBarButton(
                icon = Icons.Filled.History,
                text = AppRoute.History.route,
                onClick = {
                    navigationWithDestinationPreCheck(
                        navController,
                        AppRoute.History.route,
                        navBackStackEntry
                    )
                }
            )
            BottomNavigationBarButton(
                icon = Icons.Filled.Favorite,
                text = AppRoute.Wishlist.route,
                onClick = {
                    navigationWithDestinationPreCheck(
                        navController,
                        AppRoute.Wishlist.route,
                        navBackStackEntry
                    )
                }
            )
        }
    }
}

@Composable
fun BottomNavigationBarButton(icon: ImageVector, text: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = { onClick() }) {
            Icon(
                icon,
                contentDescription = text
            )
        }
        Text(text)
    }
}
