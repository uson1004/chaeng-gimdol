package com.yuseob.chaenggimdol.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.ui.components.BuddyMood
import com.yuseob.chaenggimdol.ui.components.BuddyStone
import com.yuseob.chaenggimdol.ui.components.SignalButton

data class PermissionUiState(
    val locationGranted: Boolean = false,
    val notificationGranted: Boolean = false,
    val locationTrackingEnabled: Boolean = false,
    val showLocationRationale: Boolean = false,
    val showNotificationRationale: Boolean = false,
) {
    val canTrackLocation: Boolean
        get() = locationGranted && locationTrackingEnabled
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    permissionState: PermissionUiState = PermissionUiState(),
    onStart: () -> Unit,
    onRegisterItems: () -> Unit,
    onRequestPermissions: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BuddyStone(
            mood = BuddyMood.Neutral,
            size = 112.dp,
            decorative = true,
        )
        Text(
            text = "오늘도 놓치는 것 없이\n가볍게 출발해요",
            style = MaterialTheme.typography.displaySmall,
        )
        Text("챙길 물건 ${state.activeItems.size}개")
        state.activeSessionId?.let {
            Text("진행 중인 챙김이 있어요")
        }
        state.message?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
            )
        }
        if (
            permissionState.locationTrackingEnabled &&
            !permissionState.locationGranted
        ) {
            Text(
                text = "위치 없이도 사용할 수 있어요",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 20.dp),
            )
            Text(
                text = "자동 출발 알림은 꺼지지만 체크리스트는 그대로 쓸 수 있어요.",
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedButton(
                onClick = if (permissionState.showLocationRationale) {
                    onOpenSettings
                } else {
                    onRequestPermissions
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(
                    if (permissionState.showLocationRationale) {
                        "권한 설정 보기"
                    } else {
                        "위치 권한 허용"
                    },
                )
            }
        }
        Spacer(Modifier.weight(1f))
        SignalButton(
            text = when {
                state.activeSessionId != null -> "챙김 계속하기"
                state.activeItems.isEmpty() -> "물건 등록하기"
                permissionState.locationTrackingEnabled &&
                    !permissionState.locationGranted -> "수동으로 시작"
                else -> "챙김 모드 시작"
            },
            onClick = when {
                state.activeItems.isEmpty() -> onRegisterItems
                else -> onStart
            },
        )
    }
}
