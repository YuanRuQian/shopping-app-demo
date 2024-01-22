package yuan.lydia.shoppingappdemo

import android.R
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import yuan.lydia.shoppingappdemo.ui.screens.canvas.AppCanvas


@RunWith(AndroidJUnit4::class)
class AppCanvasTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun testAppTopBarTitle() {


        composeTestRule.setContent {
            AppCanvas()
        }

    }
}