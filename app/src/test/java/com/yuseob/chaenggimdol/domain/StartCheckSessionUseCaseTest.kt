package com.yuseob.chaenggimdol.domain

import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.domain.session.StartCheckSessionUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class StartCheckSessionUseCaseTest {
    @Test
    fun activeItemsBecomeUncheckedSessionItems() = runTest {
        val repository = FakeSessionRepository()

        val id = StartCheckSessionUseCase(
            repository = repository,
            clock = { 1_000L },
        )(
            items = listOf(
                UserItem(1, "휴대폰", "phone", active = true),
                UserItem(2, "우산", "umbrella", active = false),
            ),
        )

        val session = repository.requireSession(id)
        assertEquals(
            listOf(CheckStatus.Unchecked),
            session.items.map { it.status },
        )
    }
}
