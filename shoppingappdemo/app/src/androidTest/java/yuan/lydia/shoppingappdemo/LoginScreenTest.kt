package yuan.lydia.shoppingappdemo

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import yuan.lydia.shoppingappdemo.ui.screens.userAuth.LoginScreen

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginButtonDisabled() {
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                navigateToRegister = {},
                showSnackBarMessage = {}
            )
        }

        composeTestRule.onNodeWithTag("loginButton")
            .assertIsNotEnabled()

        // Type username and password
        composeTestRule.onNodeWithTag("username").performTextInput("user")

        composeTestRule.onNodeWithTag("loginButton")
            .assertIsNotEnabled()

        composeTestRule.onNodeWithTag("password").performTextInput("password")

        composeTestRule.onNodeWithTag("loginButton")
            .assertIsEnabled()

        composeTestRule.onNodeWithTag("password").performTextClearance()

        composeTestRule.onNodeWithTag("loginButton")
            .assertIsNotEnabled()

        composeTestRule.onNodeWithTag("password").performTextInput("password")


        composeTestRule.onNodeWithTag("loginButton")
            .assertIsEnabled()
    }

    @Test
    fun testLoginButtonCloseKeyboard() {
        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                navigateToRegister = {},
                showSnackBarMessage = {}
            )
        }

        composeTestRule.onNodeWithTag("username").performTextInput("user")

        composeTestRule.onNodeWithTag("password").performTextInput("password")


        composeTestRule.onNodeWithTag("loginButton")
            .performClick()

        // assertFalse(isKeyboardShown())

        // TODO: Check that the keyboard is closed
    }

    @Test
    fun testNavigateToRegister() {
        // Counter to keep track of the number of times navigateToRegister is called
        var navigateToRegisterCallCount = 0

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = {},
                navigateToRegister = {
                    // Increment the counter when navigateToRegister is called
                    navigateToRegisterCallCount++
                },
                showSnackBarMessage = {}
            )
        }

        // Find the clickable text with the tag "registerText" and perform a click
        composeTestRule.onNodeWithTag("registerText").performClick()

        // Check that navigateToRegister was called once
        assertEquals(1, navigateToRegisterCallCount)
    }

    private fun isKeyboardShown(): Boolean {
        val inputMethodManager =
            InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
        return inputMethodManager.isAcceptingText
    }

}
