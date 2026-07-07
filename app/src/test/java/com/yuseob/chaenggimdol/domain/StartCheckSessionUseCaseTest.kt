package com.yuseob.chaenggimdol.domain

import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.domain.session.StartCheckSessionUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class StartCheckSessionUseCaseTest {
    @Test
    fun activeItemsBecomeUncheckedSessionItemsWithSnapshotMetadata() = runTest {
        val repository = FakeSessionRepository()

        val id = StartCheckSessionUseCase(
            repository = repository,
            clock = { 1_000L },
        )(
            items = listOf(
                UserItem(1, "휴대폰", "phone", active = true),
                UserItem(2, "우산", "umbrella", active = false),
                UserItem(
                    id = 3,
                    name = "충전기",
                    category = "charger",
                    important = false,
                    active = true,
                    checkHint = "콘센트 옆",
                ),
            ),
        )

        val session = repository.requireSession(id)
        assertEquals(
            listOf(CheckStatus.Unchecked, CheckStatus.Unchecked),
            session.items.map { it.status },
        )
        assertEquals(
            listOf(true, false),
            session.items.map { it.important },
        )
        assertEquals(
            listOf(null, "콘센트 옆"),
            session.items.map { it.checkHint },
        )
    }

    @Test
    fun sessionItemsPutImportantItemsFirst() = runTest {
        val repository = FakeSessionRepository()

        val id = StartCheckSessionUseCase(repository = repository)(
            items = listOf(
                UserItem(1, "우산", "umbrella", important = false, active = true),
                UserItem(2, "휴대폰", "phone", important = true, active = true),
            ),
        )

        assertEquals(
            listOf("휴대폰", "우산"),
            repository.requireSession(id).items.map { it.name },
        )
    }
}
