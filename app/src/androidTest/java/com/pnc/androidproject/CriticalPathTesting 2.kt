package com.pnc.androidproject

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CriticalPathTesting {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verifyCriticalPathNavigation_Home_Products_Orders_Profile() {
        // check home screen loads
        composeTestRule.onAllNodesWithText("Home").onFirst().assertIsDisplayed()

        // move to products
        composeTestRule.onNodeWithContentDescription("Products", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithText("Products").onFirst().assertIsDisplayed()
        composeTestRule.onNodeWithText("Saved").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()

        // go to orders
        composeTestRule.onNodeWithContentDescription("Orders", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithText("Orders").onFirst().assertIsDisplayed()

        // go to profile
        composeTestRule.onNodeWithContentDescription("Profile", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onAllNodesWithText("Profile").onFirst().assertIsDisplayed()
    }

    @Test
    fun verifyProductsFilterChipsDisplay() {
        // go to products
        composeTestRule.onNodeWithContentDescription("Products", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        // verify filters exist
        composeTestRule.onNodeWithText("Saved").assertIsDisplayed()
        composeTestRule.onNodeWithText("History").assertIsDisplayed()
    }
}
