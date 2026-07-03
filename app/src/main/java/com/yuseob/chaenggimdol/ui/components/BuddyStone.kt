package com.yuseob.chaenggimdol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.ui.theme.BuddyCoral
import com.yuseob.chaenggimdol.ui.theme.Charcoal

enum class BuddyMood(
    val face: String,
    val description: String,
) {
    Neutral("˙ᵕ˙", "차분하게 웃는 챙김돌"),
    Attention("•_•", "확인이 필요해 눈을 크게 뜬 챙김돌"),
    Complete("˶ᵔᵕᵔ˶", "환하게 웃는 챙김돌"),
}

@Composable
fun BuddyStone(
    mood: BuddyMood,
    modifier: Modifier = Modifier,
    size: Dp = 104.dp,
    decorative: Boolean = false,
) {
    val accessibility = if (decorative) {
        Modifier.clearAndSetSemantics {}
    } else {
        Modifier.semantics {
            contentDescription = mood.description
        }
    }

    Box(
        modifier = modifier
            .then(accessibility)
            .size(width = size, height = size * 0.84f)
            .clip(RoundedCornerShape(42))
            .background(BuddyCoral),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = mood.face,
            color = Charcoal,
        )
    }
}
