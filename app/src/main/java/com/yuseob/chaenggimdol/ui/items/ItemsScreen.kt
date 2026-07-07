package com.yuseob.chaenggimdol.ui.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.ui.components.SignalCard
import com.yuseob.chaenggimdol.ui.components.SignalChip

@Composable
fun ItemsScreen(
    state: ItemsUiState,
    onNameChange: (String) -> Unit,
    onImportantChange: (Boolean) -> Unit = {},
    onHintChange: (String) -> Unit = {},
    onAdd: () -> Unit,
    onToggleActive: (UserItem) -> Unit,
    onToggleImportant: (UserItem) -> Unit = {},
    onDelete: (Long) -> Unit,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "내 물건",
            style = MaterialTheme.typography.headlineSmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SignalChip(text = "전체 ${state.totalCount}개", selected = true)
            SignalChip(text = "사용 ${state.activeCount}개")
            SignalChip(text = "꼭 확인 ${state.activeImportantCount}개")
        }
        OutlinedTextField(
            value = state.editorName,
            onValueChange = onNameChange,
            label = { Text("물건 이름") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("꼭 확인")
            Switch(
                checked = state.editorImportant,
                onCheckedChange = onImportantChange,
                modifier = Modifier.semantics {
                    contentDescription = "새 물건 꼭 확인"
                },
            )
        }
        OutlinedTextField(
            value = state.editorCheckHint,
            onValueChange = onHintChange,
            label = { Text("챙김 힌트") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Button(
            onClick = onAdd,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
        ) {
            Text("추가하기")
        }
        state.errorMessage?.let { message ->
            SignalCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                    )
                    if (state.retryAvailable) {
                        TextButton(
                            onClick = onRetry,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary,
                            ),
                        ) {
                            Text("다시 시도")
                        }
                    }
                }
            }
        }
        SignalCard(Modifier.fillMaxWidth()) {
            Text(
                text = "매일 챙김",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = state.items,
                key = { item -> item.id },
            ) { item ->
                SignalCard(Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                maxLines = 2,
                            )
                            Text(
                                text = item.metadataLabel(),
                                style = MaterialTheme.typography.bodySmall,
                            )
                            item.checkHint?.takeIf(String::isNotBlank)?.let { hint ->
                                Text(
                                    text = hint,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                        TextButton(
                            onClick = { onToggleImportant(item) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.secondary,
                            ),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .semantics {
                                    contentDescription = "${item.name} 꼭 확인"
                                },
                        ) {
                            Text(
                                text = if (item.important) "꼭 확인" else "상황 따라",
                                modifier = Modifier.clearAndSetSemantics {},
                            )
                        }
                        Switch(
                            checked = item.active,
                            onCheckedChange = { onToggleActive(item) },
                            modifier = Modifier.semantics {
                                contentDescription = "${item.name} 챙김 사용"
                            },
                        )
                        TextButton(
                            onClick = { onDelete(item.id) },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .semantics {
                                    contentDescription = "${item.name} 삭제"
                                },
                        ) {
                            Text(
                                text = "삭제",
                                modifier = Modifier.clearAndSetSemantics {},
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun UserItem.metadataLabel(): String {
    val importance = if (important) "꼭 확인" else "상황 따라 확인"
    val status = if (active) "사용 중" else "쉬는 중"
    return "$importance · $status"
}
