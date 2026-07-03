package com.yuseob.chaenggimdol.ui.items

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.domain.item.UserItem

@Composable
fun ItemsScreen(
    state: ItemsUiState,
    onNameChange: (String) -> Unit,
    onAdd: () -> Unit,
    onToggleActive: (UserItem) -> Unit,
    onDelete: (Long) -> Unit,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Text(
            text = "내 물건",
            style = MaterialTheme.typography.displaySmall,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.editorName,
                onValueChange = onNameChange,
                label = { Text("물건 이름") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            Button(onClick = onAdd) {
                Text("추가하기")
            }
        }
        state.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
            )
            if (state.retryAvailable) {
                TextButton(onClick = onRetry) {
                    Text("다시 시도")
                }
            }
        }
        LazyColumn {
            items(
                items = state.items,
                key = { item -> item.id },
            ) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.name,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                    )
                    Switch(
                        checked = item.active,
                        onCheckedChange = { onToggleActive(item) },
                        modifier = Modifier.semantics {
                            contentDescription = "${item.name} 챙김 사용"
                        },
                    )
                    IconButton(
                        onClick = { onDelete(item.id) },
                        modifier = Modifier.semantics {
                            contentDescription = "${item.name} 삭제"
                        },
                    ) {
                        Text("삭제")
                    }
                }
            }
        }
    }
}
