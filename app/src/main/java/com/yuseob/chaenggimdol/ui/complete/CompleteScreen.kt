package com.yuseob.chaenggimdol.ui.complete

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yuseob.chaenggimdol.ui.components.BuddyMood
import com.yuseob.chaenggimdol.ui.components.BuddyStone
import com.yuseob.chaenggimdol.ui.components.SignalButton

@Composable
fun CompleteScreen(
    checkedCount: Int,
    onHome: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BuddyStone(
            mood = BuddyMood.Complete,
            size = 120.dp,
            decorative = true,
        )
        Text(
            text = "좋아, 오늘도\n빠뜨린 것 없어요!",
            style = MaterialTheme.typography.displaySmall,
        )
        Text("${checkedCount}개 물건 확인 완료")
        Spacer(Modifier.weight(1f))
        SignalButton(
            text = "홈으로 돌아가기",
            onClick = onHome,
        )
    }
}
