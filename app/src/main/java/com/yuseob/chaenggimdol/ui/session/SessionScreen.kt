package com.yuseob.chaenggimdol.ui.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.ui.components.BuddyMood
import com.yuseob.chaenggimdol.ui.components.BuddyStone
import com.yuseob.chaenggimdol.ui.components.SignalButton

@Composable
fun SessionScreen(
    state: SessionUiState,
    onTogglePacked: (Long) -> Unit,
    onNotApplicable: (Long) -> Unit,
    onRequestComplete: () -> Unit,
    onDismissIncomplete: () -> Unit,
    onConfirmComplete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        BuddyStone(
            mood = BuddyMood.Attention,
            size = 72.dp,
            decorative = true,
        )
        Text("잠깐! 놓고 가는 거 없지?")
        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            items(
                items = state.items,
                key = { item -> item.itemId },
            ) { item ->
                val statusLabel = when (item.status) {
                    CheckStatus.Unchecked -> "미확인"
                    CheckStatus.Packed -> "챙김"
                    CheckStatus.NotApplicable -> "해당 없음"
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp)
                            .semantics(mergeDescendants = true) {
                                contentDescription = "${item.name} 챙김 상태 변경"
                                stateDescription = statusLabel
                                role = Role.Button
                            }
                            .clickable {
                                onTogglePacked(item.itemId)
                            }
                            .padding(vertical = 8.dp),
                    ) {
                        Text(item.name)
                        Text(statusLabel)
                    }
                    TextButton(
                        onClick = {
                            onNotApplicable(item.itemId)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary,
                        ),
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .semantics {
                                contentDescription = "${item.name} 해당 없음"
                            },
                    ) {
                        Text(
                            text = "해당 없음",
                            modifier = Modifier.clearAndSetSemantics {},
                        )
                    }
                }
            }
        }
        SignalButton(
            text = "다 챙겼어요",
            onClick = onRequestComplete,
        )
    }

    state.confirmIncompleteCount?.let { count ->
        AlertDialog(
            onDismissRequest = onDismissIncomplete,
            title = {
                Text("확인하지 않은 물건이 ${count}개 있어요")
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmComplete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ),
                ) {
                    Text("그대로 완료")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissIncomplete,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                    ),
                ) {
                    Text("계속 확인")
                }
            },
        )
    }
}
