package com.yuseob.chaenggimdol.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.ui.items.ItemsScreen
import com.yuseob.chaenggimdol.ui.items.ItemsUiState
import org.junit.Rule
import org.junit.Test

class ItemsScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun editorAndAddActionRemainVisibleAtLargeFontScale() {
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = density.density,
                    fontScale = 2f,
                ),
            ) {
                ItemsScreen(
                    state = ItemsUiState(),
                    onNameChange = {},
                    onAdd = {},
                    onToggleActive = {},
                    onDelete = {},
                )
            }
        }

        compose.onNodeWithText("물건 이름").assertIsDisplayed()
        compose.onNodeWithText("추가하기").assertIsDisplayed()
    }

    @Test
    fun deleteActionHasItemSpecificAccessibleLabel() {
        compose.setContent {
            ItemsScreen(
                state = ItemsUiState(
                    items = listOf(UserItem(1, "휴대폰", "phone")),
                ),
                onNameChange = {},
                onAdd = {},
                onToggleActive = {},
                onDelete = {},
            )
        }

        compose
            .onNode(hasContentDescription("휴대폰 삭제"))
            .assertIsDisplayed()
            .assertHasClickAction()
        compose
            .onAllNodes(hasContentDescription("휴대폰 삭제") and hasText("삭제"))
            .assertCountEquals(0)
    }

    @Test
    fun itemMetadataDoesNotConfuseActiveWithImportant() {
        compose.setContent {
            ItemsScreen(
                state = ItemsUiState(
                    items = listOf(
                        UserItem(
                            id = 1,
                            name = "우산",
                            category = "umbrella",
                            important = false,
                            active = true,
                            checkHint = "현관 앞",
                        ),
                    ),
                ),
                onNameChange = {},
                onAdd = {},
                onToggleActive = {},
                onDelete = {},
            )
        }

        compose.onNodeWithText("상황 따라 확인 · 사용 중").assertIsDisplayed()
        compose.onNodeWithText("현관 앞").assertIsDisplayed()
    }
}
