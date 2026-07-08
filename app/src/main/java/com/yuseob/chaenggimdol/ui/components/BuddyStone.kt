package com.yuseob.chaenggimdol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

enum class BuddyDepth {
    Flat,
    Raised,
}

@Composable
fun BuddyStone(
    mood: BuddyMood,
    modifier: Modifier = Modifier,
    size: Dp = 104.dp,
    decorative: Boolean = false,
    depth: BuddyDepth = BuddyDepth.Raised,
) {
    val accessibility = if (decorative) {
        Modifier.clearAndSetSemantics {}
    } else {
        Modifier.semantics {
            contentDescription = mood.description
        }
    }

    val stoneShape = RoundedCornerShape(42)
    val stoneHeight = size * 0.84f

    Box(
        modifier = modifier
            .then(accessibility)
            .size(width = size, height = stoneHeight),
        contentAlignment = Alignment.Center,
    ) {
        if (depth == BuddyDepth.Raised) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(y = 5.dp)
                    .clip(stoneShape)
                    .background(Charcoal.copy(alpha = 0.16f)),
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .then(
                    if (depth == BuddyDepth.Raised) {
                        Modifier.shadow(
                            elevation = 8.dp,
                            shape = stoneShape,
                            clip = false,
                        )
                    } else {
                        Modifier
                    },
                )
                .clip(stoneShape)
                .background(BuddyCoral),
        )
        if (depth == BuddyDepth.Raised) {
            Box(
                modifier = Modifier
                    .size(width = size * 0.68f, height = stoneHeight * 0.30f)
                    .align(Alignment.TopCenter)
                    .offset(y = 8.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.22f)),
            )
        }
        Text(
            text = mood.face,
            color = Charcoal,
        )
    }
}
