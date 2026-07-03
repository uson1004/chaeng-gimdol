package com.yuseob.chaenggimdol.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yuseob.chaenggimdol.ui.components.BuddyMood
import com.yuseob.chaenggimdol.ui.components.BuddyStone
import com.yuseob.chaenggimdol.ui.components.SignalButton
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SignalComponentsTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun buddyIsHiddenFromAccessibilityWhenDecorative() {
        compose.setContent {
            BuddyStone(mood = BuddyMood.Neutral, decorative = true)
        }

        val nodes = compose
            .onAllNodesWithContentDescription("차분하게 웃는 챙김돌")
            .fetchSemanticsNodes()
        assertTrue(nodes.isEmpty())
    }

    @Test
    fun signalButtonExposesLabelAndClick() {
        var clicked = false
        compose.setContent {
            SignalButton(
                text = "챙김 모드 시작",
                onClick = { clicked = true },
            )
        }

        compose
            .onNodeWithText("챙김 모드 시작")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()
        assertTrue(clicked)
    }
}
