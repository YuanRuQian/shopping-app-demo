package yuan.lydia.shoppingappdemo.ui.screens.canvas

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import yuan.lydia.shoppingappdemo.data.utils.UserInfoManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    isUserLoggedIn: Boolean,
    showSnackbarMessage: (String) -> Unit,
    setIsUserLoggedIn: (Boolean) -> Unit
) {
    val context = LocalContext.current
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val destination = navBackStackEntry?.destination?.route
            if (!isUserLoggedIn) {
                Text("Shopping App Demo by Lydia Yuan")
            } else {
                Text(text = getRouteDisplayName(destination))
            }
        },
        actions = {
            ExitButton(
                isUserLoggedIn,
                context,
                navController,
                showSnackbarMessage,
                setIsUserLoggedIn
            )
        },
    )
}


@Composable
fun ExitButton(
    isUserLoggedIn: Boolean,
    context: Context,
    navController: NavController,
    showSnackbarMessage: (String) -> Unit,
    setIsUserLoggedIn: (Boolean) -> Unit
) {
    if (isUserLoggedIn) {
        IconButton(onClick = {
            logout(
                context,
                navController,
                showSnackbarMessage,
                setIsUserLoggedIn
            )
        }) {
            Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = "Log out and exit the app"
            )
        }
    }
}

fun logout(
    context: Context,
    navController: NavController,
    showSnackbarMessage: (String) -> Unit,
    setIsUserLoggedIn: (Boolean) -> Unit
) {
    val userInfoManager = UserInfoManager.getInstance(context)
    userInfoManager.clearToken()
    val currentUsername = userInfoManager.getUsername()
    userInfoManager.clearUsername()
    setIsUserLoggedIn(false)
    navController.navigate(AppRoute.Login.route) {
        popUpTo(navController.graph.startDestinationId) {
            inclusive = true
        }
        launchSingleTop = true
    }
    showSnackbarMessage("See you next time, $currentUsername! 🫡")
}
