package com.yuseob.chaenggimdol.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.ui.components.SignalCard
import com.yuseob.chaenggimdol.ui.components.SignalChip

data class SettingsUiState(
    val locationTrackingEnabled: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
)

@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onLocationTrackingChanged: (Boolean) -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onRequestDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "설정",
            style = MaterialTheme.typography.headlineSmall,
        )
        SignalCard(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "출발 감지",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "챙김 모드를 시작한 동안만 위치를 사용해요.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Switch(
                    checked = state.locationTrackingEnabled,
                    onCheckedChange = onLocationTrackingChanged,
                )
            }
        }
        SignalCard(Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "자동 알림 신뢰 상태",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "알림 권한, 위치 권한, 출발 감지가 모두 켜져야 자동 출발 알림을 받을 수 있어요.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SignalChip(text = "알림 켜짐", selected = true)
                    SignalChip(text = if (state.locationTrackingEnabled) "위치 사용" else "위치 꺼짐")
                    SignalChip(text = "좌표 저장 안 함")
                }
            }
        }
        OutlinedButton(
            onClick = onOpenNotificationSettings,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary,
            ),
        ) {
            Text("알림 설정 열기")
        }
        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = onRequestDelete,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
            ),
        ) {
            Text("내 데이터 모두 삭제")
        }
    }

    if (state.showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text("정말 삭제할까요?") },
            text = { Text("등록한 물건과 챙김 기록이 모두 사라져요.") },
            confirmButton = {
                TextButton(
                    onClick = onConfirmDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissDelete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ),
                ) {
                    Text("취소")
                }
            },
        )
    }
}
