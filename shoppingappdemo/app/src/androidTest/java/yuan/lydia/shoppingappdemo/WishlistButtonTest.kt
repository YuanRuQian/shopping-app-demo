package yuan.lydia.shoppingappdemo

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import yuan.lydia.shoppingappdemo.ui.common.WishlistButton

@RunWith(AndroidJUnit4::class)
class WishlistButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInWishlist() {

        composeTestRule.setContent {
            WishlistButton(
                productName = "Test Product",
                isInWishlist = true,
                addToWishList = { _, _, _, _ -> },
                removeFromWishList = { _, _, _, _ -> },
                productId = 1,
                showSnackbarMessage = {},
                token = "token"
            )
        }

        composeTestRule.onNodeWithText("Remove from Wishlist").assertExists()

        val iconTag = "wishlistButtonIcon"

        composeTestRule.onNodeWithTag(iconTag, true).printToLog("TAG")

        composeTestRule.onNodeWithTag(iconTag, true).assertExists()

        // TODO: how to check if the icon is the correct one? no solution for Jetpack Compose Icon yet

    }

    @Test
    fun testNotInWishlist() {
        // Test when isInWishlist is false
        composeTestRule.setContent {
            WishlistButton(
                productName = "Test Product",
                isInWishlist = false,
                addToWishList = { _, _, _, _ -> },
                removeFromWishList = { _, _, _, _ -> },
                productId = 1,
                showSnackbarMessage = {},
                token = "token"
            )
        }
        // Verify that the button text is "Add to Wishlist" when isInWishlist is false
        composeTestRule.onNodeWithText("Add to Wishlist").assertExists()
    }
}