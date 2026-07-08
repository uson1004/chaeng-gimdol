package com.yuseob.chaenggimdol.ui

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import com.yuseob.chaenggimdol.ui.components.BuddyDepth
import com.yuseob.chaenggimdol.ui.components.BuddyMood
import com.yuseob.chaenggimdol.ui.components.BuddyStone
import com.yuseob.chaenggimdol.ui.components.SignalButton
import com.yuseob.chaenggimdol.ui.components.SignalSnackbarHost
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SignalComponentsTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun buddyIsHiddenFromAccessibilityWhenDecorative() {
        compose.setContent {
            BuddyStone(
                mood = BuddyMood.Neutral,
                decorative = true,
                depth = BuddyDepth.Raised,
            )
        }

        val nodes = compose
            .onAllNodesWithContentDescription("차분하게 웃는 챙김돌")
            .fetchSemanticsNodes()
        assertTrue(nodes.isEmpty())
    }

    @Test
    fun raisedBuddyKeepsMeaningfulAccessibilityLabel() {
        compose.setContent {
            BuddyStone(
                mood = BuddyMood.Complete,
                decorative = false,
                depth = BuddyDepth.Raised,
            )
        }

        compose
            .onNodeWithContentDescription("환하게 웃는 챙김돌")
            .assertIsDisplayed()
    }

    @Test
    fun signalSnackbarHostAnnouncesPolitely() {
        compose.setContent {
            val hostState = remember { SnackbarHostState() }
            SignalSnackbarHost(hostState = hostState)
        }

        compose
            .onNodeWithTag("signal-snackbar-host")
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.LiveRegion,
                    LiveRegionMode.Polite,
                ),
            )
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
