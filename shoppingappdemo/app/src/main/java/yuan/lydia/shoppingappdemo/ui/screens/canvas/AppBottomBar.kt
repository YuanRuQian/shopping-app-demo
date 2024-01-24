package yuan.lydia.shoppingappdemo.ui.screens.canvas

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

fun navigationWithDestinationPreCheck(
    setCurrentRoute: (String) -> Unit,
    navController: NavController,
    destination: String,
    navBackStackEntry: NavBackStackEntry?
) {
    if (navBackStackEntry?.destination?.route != destination) {
        setCurrentRoute(destination)
        navController.navigate(destination)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, isUserLoggedIn: Boolean, currentRoute: String, setCurrentRoute: (String) -> Unit) {
    if (!isUserLoggedIn) {
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val icons = listOf(
        Icons.Default.Search,
        Icons.Default.ShoppingCart,
        Icons.Default.History,
        Icons.Default.Favorite
    )

    val routes = listOf(
        AppRoute.Shopping.route,
        AppRoute.Cart.route,
        AppRoute.History.route,
        AppRoute.Wishlist.route
    )

    val zippedList = icons.zip(routes)


    NavigationBar {
        zippedList.forEachIndexed { _, (icon, route) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = route) },
                label = { Text(route) },
                selected = currentRoute == route,
                onClick = {
                    navigationWithDestinationPreCheck(
                        setCurrentRoute,
                        navController,
                        route,
                        navBackStackEntry
                    )
                }
            )
        }
    }
}

