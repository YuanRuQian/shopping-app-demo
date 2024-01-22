package yuan.lydia.shoppingappdemo

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.LoginScreen

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginSuccess() {
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                navigateToRegister = {},
                showSnackBarMessage = {}
            )
        }

        // Type username and password
        composeTestRule.onNodeWithTag("username").performTextInput("testUser")
        composeTestRule.onNodeWithTag("password").performTextInput("testPassword")

        // Click login button
        composeTestRule.onNodeWithTag("loginButton").performClick()

        // Assert your expected behavior based on a successful login
    }

    @Test
    fun testLoginError() {
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                navigateToRegister = {},
                showSnackBarMessage = {}
            )
        }

        // Type username and password
        composeTestRule.onNodeWithTag("username").performTextInput("testUser")
        composeTestRule.onNodeWithTag("password").performTextInput("testPassword")

        // Click login button
        composeTestRule.onNodeWithTag("loginButton").performClick()

        // Assert your expected behavior based on a login error
    }

    // Add more tests for different scenarios
}
