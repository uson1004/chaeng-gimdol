package com.yuseob.chaenggimdol.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import com.yuseob.chaenggimdol.domain.session.CheckSessionItem
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.ui.session.SessionScreen
import com.yuseob.chaenggimdol.ui.session.SessionUiState
import org.junit.Rule
import org.junit.Test

class AccessibilityFlowTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun itemStatusIsMergedAndDecorativeBuddyIsSilent() {
        compose.setContent {
            SessionScreen(
                state = sessionState(),
                onTogglePacked = {},
                onNotApplicable = {},
                onRequestComplete = {},
                onDismissIncomplete = {},
                onConfirmComplete = {},
            )
        }

        compose.onNode(
            hasText("휴대폰") and hasStateDescription("미확인"),
        ).assertIsDisplayed()
        compose.onAllNodes(
            hasContentDescription("확인이 필요해 눈을 크게 뜬 챙김돌"),
        ).assertCountEquals(0)
    }

    @Test
    fun completionActionRemainsVisibleAtLargeFontScale() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = density.density,
                    fontScale = 2f,
                ),
            ) {
                SessionScreen(
                    state = sessionState(),
                    onTogglePacked = {},
                    onNotApplicable = {},
                    onRequestComplete = {},
                    onDismissIncomplete = {},
                    onConfirmComplete = {},
                )
            }
        }

        compose
            .onNodeWithText("다 챙겼어요")
            .assertIsDisplayed()
    }

    private fun sessionState() = SessionUiState(
        sessionId = 1,
        items = listOf(
            CheckSessionItem(
                itemId = 1,
                name = "휴대폰",
                status = CheckStatus.Unchecked,
            ),
        ),
    )
}
