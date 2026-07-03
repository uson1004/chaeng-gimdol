package com.yuseob.chaenggimdol.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    ) {
        Text(
            text = "설정",
            style = MaterialTheme.typography.displaySmall,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = "출발 감지",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "위치는 챙김 모드를 시작한 동안만 사용해요.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Switch(
                checked = state.locationTrackingEnabled,
                onCheckedChange = onLocationTrackingChanged,
            )
        }
        HorizontalDivider(Modifier.padding(vertical = 20.dp))
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
        Text(
            text = "좌표는 저장하지 않고, 계정이나 외부 서버를 사용하지 않아요.",
            style = MaterialTheme.typography.bodySmall,
        )
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
