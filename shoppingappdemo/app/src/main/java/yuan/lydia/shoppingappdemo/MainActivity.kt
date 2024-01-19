package yuan.lydia.shoppingappdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import yuan.lydia.shoppingappdemo.ui.screens.shopping.ProductsScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.LoginScreen
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.RegisterScreen
import yuan.lydia.shoppingappdemo.ui.theme.ShoppingappdemoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShoppingappdemoTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("products") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                navigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen()
                        }
                        composable("products") {
                            ProductsScreen()
                        }
                    }
                }
            }
        }
    }
}
