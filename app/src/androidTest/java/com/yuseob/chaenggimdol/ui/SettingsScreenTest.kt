package com.yuseob.chaenggimdol.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yuseob.chaenggimdol.ui.settings.SettingsScreen
import com.yuseob.chaenggimdol.ui.settings.SettingsUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun settingsExplainsPrivacyAndConfirmsDataDeletion() {
        var deleteRequested = false
        var state by mutableStateOf(
            SettingsUiState(
                locationTrackingEnabled = true,
            ),
        )
        compose.setContent {
            SettingsScreen(
                state = state,
                onLocationTrackingChanged = {},
                onOpenNotificationSettings = {},
                onRequestDelete = {
                    state = state.copy(showDeleteConfirmation = true)
                },
                onDismissDelete = {
                    state = state.copy(showDeleteConfirmation = false)
                },
                onConfirmDelete = { deleteRequested = true },
            )
        }

        compose
            .onNodeWithText("위치는 챙김 모드를 시작한 동안만 사용해요.")
            .assertIsDisplayed()
        compose
            .onNodeWithText("내 데이터 모두 삭제")
            .performClick()
        compose
            .onNodeWithText("정말 삭제할까요?")
            .assertIsDisplayed()
        compose
            .onNodeWithText("삭제")
            .performClick()

        assertTrue(deleteRequested)
    }
}
