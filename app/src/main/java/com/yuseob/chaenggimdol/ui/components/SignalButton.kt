package com.yuseob.chaenggimdol.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.ui.theme.Charcoal
import com.yuseob.chaenggimdol.ui.theme.SignalLime

@Composable
fun SignalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = SignalLime,
            contentColor = Charcoal,
        ),
    ) {
        Text(text = text)
    }
}
