package com.yuseob.chaenggimdol.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.ui.components.BuddyMood
import com.yuseob.chaenggimdol.ui.components.BuddyStone
import com.yuseob.chaenggimdol.ui.components.SignalButton

private val templates = listOf(
    "휴대폰",
    "지갑",
    "이어폰",
    "충전기",
    "우산",
    "노트북",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    state: OnboardingUiState,
    onToggle: (String) -> Unit,
    onToggleImportant: (String) -> Unit = {},
    onHintChange: (String, String) -> Unit = { _, _ -> },
    onComplete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(28.dp))
        BuddyStone(
            mood = BuddyMood.Neutral,
            size = 128.dp,
            decorative = true,
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = "자주 챙기는 물건을 골라봐요",
            style = MaterialTheme.typography.displaySmall,
        )
        Spacer(Modifier.height(20.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            templates.forEach { name ->
                FilterChip(
                    selected = name in state.selected,
                    onClick = { onToggle(name) },
                    label = { Text(name) },
                )
            }
        }
        state.selected.forEach { name ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FilterChip(
                        selected = name !in state.optional,
                        onClick = {
                            if (name in state.optional) onToggleImportant(name)
                        },
                        label = { Text("꼭 확인") },
                    )
                    FilterChip(
                        selected = name in state.optional,
                        onClick = {
                            if (name !in state.optional) onToggleImportant(name)
                        },
                        label = { Text("상황 따라") },
                    )
                }
                OutlinedTextField(
                    value = state.hints[name].orEmpty(),
                    onValueChange = { onHintChange(name, it) },
                    label = { Text("$name 힌트") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        }
        state.errorMessage?.let { message ->
            Spacer(Modifier.height(12.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(Modifier.weight(1f))
        SignalButton(
            text = "선택 완료",
            onClick = onComplete,
            enabled = !state.saving,
        )
    }
}
