package com.yuseob.chaenggimdol.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasStateDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.yuseob.chaenggimdol.domain.session.CheckSessionItem
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.ui.session.SessionScreen
import com.yuseob.chaenggimdol.ui.session.SessionUiState
import org.junit.Rule
import org.junit.Test

class SessionScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun sessionShowsItemsAndCompletionAction() {
        compose.setContent {
            SessionScreen(
                state = SessionUiState(
                    sessionId = 1,
                    items = listOf(
                        CheckSessionItem(
                            itemId = 1,
                            name = "휴대폰",
                            status = CheckStatus.Unchecked,
                        ),
                    ),
                ),
                onTogglePacked = {},
                onNotApplicable = {},
                onRequestComplete = {},
                onDismissIncomplete = {},
                onConfirmComplete = {},
            )
        }

        compose.onNodeWithText("휴대폰").assertIsDisplayed()
        compose.onNodeWithText("다 챙겼어요").assertIsDisplayed()
    }

    @Test
    fun sessionGroupsImportantItemsAndShowsHints() {
        compose.setContent {
            SessionScreen(
                state = SessionUiState(
                    sessionId = 1,
                    items = listOf(
                        CheckSessionItem(
                            itemId = 1,
                            name = "휴대폰",
                            status = CheckStatus.Unchecked,
                            important = true,
                            checkHint = "가방 안",
                        ),
                        CheckSessionItem(
                            itemId = 2,
                            name = "우산",
                            status = CheckStatus.Unchecked,
                            important = false,
                        ),
                    ),
                ),
                onTogglePacked = {},
                onNotApplicable = {},
                onRequestComplete = {},
                onDismissIncomplete = {},
                onConfirmComplete = {},
            )
        }

        compose.onNodeWithText("꼭 확인할 것").assertIsDisplayed()
        compose.onNodeWithText("상황 따라 확인").assertIsDisplayed()
        compose.onNodeWithText("가방 안").assertIsDisplayed()
    }

    @Test
    fun itemPrimaryAndNotApplicableActionsAreSeparatedForAccessibility() {
        compose.setContent {
            SessionScreen(
                state = SessionUiState(
                    sessionId = 1,
                    items = listOf(
                        CheckSessionItem(
                            itemId = 1,
                            name = "휴대폰",
                            status = CheckStatus.Unchecked,
                        ),
                    ),
                ),
                onTogglePacked = {},
                onNotApplicable = {},
                onRequestComplete = {},
                onDismissIncomplete = {},
                onConfirmComplete = {},
            )
        }

        compose
            .onNode(
                hasText("휴대폰") and
                    hasStateDescription("미확인") and
                    hasContentDescription("휴대폰 챙김 상태 변경"),
            )
            .assertIsDisplayed()
            .assertHasClickAction()
        compose
            .onNode(hasContentDescription("휴대폰 해당 없음"))
            .assertIsDisplayed()
            .assertHasClickAction()
    }
}
