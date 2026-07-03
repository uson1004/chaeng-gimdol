package com.yuseob.chaenggimdol.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.yuseob.chaenggimdol.navigation.AppRoute
import com.yuseob.chaenggimdol.navigation.TopLevelNavigationBar
import org.junit.Rule
import org.junit.Test

class NavigationBarTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun topLevelNavigationUsesLabelsWithoutTextBulletIcons() {
        compose.setContent {
            TopLevelNavigationBar(
                selected = AppRoute.Home,
                onSelect = {},
            )
        }

        compose.onNodeWithText("오늘").assertIsDisplayed()
        compose.onNodeWithText("내 물건").assertIsDisplayed()
        compose.onNodeWithText("설정").assertIsDisplayed()
        compose.onAllNodes(hasText("●")).assertCountEquals(0)
    }
}
