package com.yuseob.chaenggimdol.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.yuseob.chaenggimdol.ui.onboarding.OnboardingScreen
import com.yuseob.chaenggimdol.ui.onboarding.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class OnboardingScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun selectedTemplatesAreReturned() {
        var state by mutableStateOf(
            OnboardingUiState(selected = emptySet()),
        )
        var completed: Set<String>? = null
        compose.setContent {
            OnboardingScreen(
                state = state,
                onToggle = { name ->
                    state = state.copy(
                        selected = if (name in state.selected) {
                            state.selected - name
                        } else {
                            state.selected + name
                        },
                    )
                },
                onComplete = { completed = state.selected },
            )
        }

        listOf("휴대폰", "이어폰", "충전기").forEach { name ->
            compose.onNodeWithText(name).performClick()
        }
        compose.onNodeWithText("선택 완료").performClick()

        assertEquals(
            setOf("휴대폰", "이어폰", "충전기"),
            completed,
        )
    }

    @Test
    fun selectedItemCanCollectImportanceAndHint() {
        var state by mutableStateOf(
            OnboardingUiState(
                selected = setOf("휴대폰"),
                optional = emptySet(),
                hints = emptyMap(),
            ),
        )
        compose.setContent {
            OnboardingScreen(
                state = state,
                onToggle = {},
                onToggleImportant = { name ->
                    state = state.copy(optional = state.optional + name)
                },
                onHintChange = { name, hint ->
                    state = state.copy(hints = state.hints + (name to hint))
                },
                onComplete = {},
            )
        }

        compose.onNodeWithText("상황 따라").performClick()
        compose.onNodeWithText("휴대폰 힌트").performClick()
        compose.onNodeWithText("휴대폰 힌트").performTextInput("가방 안")

        assertEquals(setOf("휴대폰"), state.optional)
        assertEquals("가방 안", state.hints["휴대폰"])
    }
}
