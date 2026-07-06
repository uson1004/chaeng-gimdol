package com.yuseob.chaenggimdol.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.ui.home.HomeScreen
import com.yuseob.chaenggimdol.ui.home.HomeUiState
import com.yuseob.chaenggimdol.ui.home.PermissionUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun primaryActionStartsCheckSession() {
        var clicked = false
        compose.setContent {
            HomeScreen(
                state = HomeUiState(
                    activeItems = listOf(
                        UserItem(1, "휴대폰", "phone"),
                    ),
                ),
                permissionState = PermissionUiState(),
                onStart = { clicked = true },
                onRegisterItems = {},
                onRequestPermissions = {},
                onOpenSettings = {},
            )
        }

        compose
            .onNodeWithText("챙김 모드 시작")
            .assertIsDisplayed()
            .performClick()
        assertTrue(clicked)
    }

    @Test
    fun deniedLocationKeepsManualChecklistAvailable() {
        var started = false
        var settingsOpened = false
        compose.setContent {
            HomeScreen(
                state = HomeUiState(
                    activeItems = listOf(
                        UserItem(1, "휴대폰", "phone"),
                    ),
                ),
                permissionState = PermissionUiState(
                    locationGranted = false,
                    notificationGranted = false,
                    locationTrackingEnabled = true,
                    showLocationRationale = true,
                ),
                onStart = { started = true },
                onRegisterItems = {},
                onRequestPermissions = {},
                onOpenSettings = { settingsOpened = true },
            )
        }

        compose
            .onNodeWithText("위치 미허용")
            .assertIsDisplayed()
        compose
            .onNodeWithText("수동으로 시작")
            .performClick()
        compose
            .onNodeWithText("권한 설정 보기")
            .performClick()

        assertTrue(started)
        assertTrue(settingsOpened)
    }
}
