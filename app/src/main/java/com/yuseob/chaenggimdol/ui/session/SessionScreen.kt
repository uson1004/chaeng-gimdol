package com.yuseob.chaenggimdol.ui.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.yuseob.chaenggimdol.ui.components.SignalCard
import com.yuseob.chaenggimdol.ui.components.SignalChip

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
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BuddyStone(
            mood = BuddyMood.Attention,
            size = 58.dp,
            decorative = true,
        )
        Text(
            text = "잠깐! 놓고 가는 거 없지?",
            style = MaterialTheme.typography.headlineSmall,
        )
        SignalCard(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${state.checkedCount} / ${state.items.size}개 확인",
                    style = MaterialTheme.typography.titleMedium,
                )
                SignalChip(text = "미확인 ${state.uncheckedCount}개")
            }
            Text(
                text = "꼭 확인 ${state.uncheckedImportantCount}개 · 상황 따라 ${state.uncheckedOptionalCount}개 남음",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        val importantItems = state.items.filter { it.important }
        val optionalItems = state.items.filterNot { it.important }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            checkGroup(
                title = "꼭 확인할 것",
                items = importantItems,
                onTogglePacked = onTogglePacked,
                onNotApplicable = onNotApplicable,
            )
            checkGroup(
                title = "상황 따라 확인",
                items = optionalItems,
                onTogglePacked = onTogglePacked,
                onNotApplicable = onNotApplicable,
            )
        }
        if (state.uncheckedCount > 0) {
            SignalCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "미확인 물건이 남아 있어요",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "완료 전에 한 번 더 확인할 수 있게 남겨둡니다.",
                        style = MaterialTheme.typography.bodySmall,
                    )
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

private fun androidx.compose.foundation.lazy.LazyListScope.checkGroup(
    title: String,
    items: List<com.yuseob.chaenggimdol.domain.session.CheckSessionItem>,
    onTogglePacked: (Long) -> Unit,
    onNotApplicable: (Long) -> Unit,
) {
    if (items.isEmpty()) return
    item(key = "$title-header") {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
    }
    items(
        items = items,
        key = { item -> item.itemId },
    ) { item ->
        val statusLabel = when (item.status) {
            CheckStatus.Unchecked -> "미확인"
            CheckStatus.Packed -> "챙김"
            CheckStatus.NotApplicable -> "해당 없음"
        }
        SignalCard(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .semantics(mergeDescendants = true) {
                            contentDescription = "${item.name} 챙김 상태 변경"
                            stateDescription = statusLabel
                            role = Role.Button
                        }
                        .clickable {
                            onTogglePacked(item.itemId)
                        },
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    item.checkHint?.takeIf(String::isNotBlank)?.let { hint ->
                        Text(
                            text = hint,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                SignalChip(
                    text = statusLabel,
                    selected = item.status == CheckStatus.Packed,
                )
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
}
