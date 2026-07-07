package com.yuseob.chaenggimdol.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.ui.components.BuddyMood
import com.yuseob.chaenggimdol.ui.components.BuddyStone
import com.yuseob.chaenggimdol.ui.components.SignalButton
import com.yuseob.chaenggimdol.ui.components.SignalCard
import com.yuseob.chaenggimdol.ui.components.SignalChip

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
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BuddyStone(
            mood = if (state.activeSessionId == null) BuddyMood.Neutral else BuddyMood.Attention,
            size = 78.dp,
            decorative = true,
        )
        Text(
            text = if (state.activeSessionId == null) {
                "출발 준비 상태"
            } else {
                "진행 중인 챙김이 있어요"
            },
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            text = if (state.activeSessionId == null) {
                "오늘 챙길 물건 ${state.activeItems.size}개"
            } else {
                "확인 안 한 물건을 이어서 볼 수 있어요"
            },
            style = MaterialTheme.typography.bodyMedium,
        )
        state.message?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
            )
        }
        SignalCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (state.activeSessionId == null) "준비됨" else "계속 확인하면 돼요",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (state.activeSessionId == null) {
                        "꼭 확인 ${state.activeImportantCount}개 · 상황 따라 ${state.activeOptionalCount}개를 준비했어요."
                    } else {
                        "아직 미확인인 물건을 챙김 세션에서 이어서 확인할 수 있어요."
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SignalChip(
                        text = if (permissionState.notificationGranted) "알림 켜짐" else "알림 꺼짐",
                        selected = permissionState.notificationGranted,
                    )
                    SignalChip(
                        text = if (permissionState.locationGranted) "위치 허용" else "위치 미허용",
                        selected = permissionState.locationGranted,
                    )
                    SignalChip(
                        text = if (permissionState.locationTrackingEnabled) "감지 켜짐" else "감지 꺼짐",
                        selected = permissionState.locationTrackingEnabled,
                    )
                }
            }
        }
        SignalCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "먼저 볼 물건",
                    style = MaterialTheme.typography.titleMedium,
                )
                val previewItems = state.activeItems.take(4)
                if (previewItems.isEmpty()) {
                    Text(
                        text = "아직 등록된 물건이 없어요.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else {
                    previewItems.chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { item ->
                                SignalChip(text = item.name)
                            }
                        }
                    }
                }
            }
        }
        if (
            permissionState.locationTrackingEnabled &&
            !permissionState.locationGranted
        ) {
            OutlinedButton(
                onClick = if (permissionState.showLocationRationale) {
                    onOpenSettings
                } else {
                    onRequestPermissions
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                ),
                modifier = Modifier.fillMaxWidth(),
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
        OutlinedButton(
            onClick = onRegisterItems,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary,
            ),
        ) {
            Text("내 물건 정리")
        }
    }
}
