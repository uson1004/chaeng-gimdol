package com.yuseob.chaenggimdol.ui

import androidx.compose.ui.test.assertIsDisplayed
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
}
