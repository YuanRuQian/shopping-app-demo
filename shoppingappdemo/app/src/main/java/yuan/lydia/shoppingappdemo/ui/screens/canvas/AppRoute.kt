package yuan.lydia.shoppingappdemo.ui.screens.canvas

sealed class AppRoute(val route: String) {
    data object Login : AppRoute("Login")
    data object Register : AppRoute("Register")
    data object Shopping : AppRoute("Shopping")

    data object Cart : AppRoute("Cart")
    data object History : AppRoute("History")
    data object Wishlist : AppRoute("Wishlist")

    data object OrderDetails : AppRoute("Order Details")
}

fun getRouteDisplayName(route: String?): String {
    if (route == null) {
        return "Shopping App Demo by Lydia Yuan"
    }
    val routeSegments = route.split("/")
    return if (routeSegments.isNotEmpty()) {
        routeSegments[0]
    } else {
        route
    }
}