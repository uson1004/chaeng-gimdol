package com.yuseob.chaenggimdol.ui

import com.yuseob.chaenggimdol.domain.FakeSessionRepository
import com.yuseob.chaenggimdol.domain.item.ItemRepository
import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.domain.session.CheckSession
import com.yuseob.chaenggimdol.domain.session.CheckSessionItem
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.domain.session.StartCheckSessionUseCase
import com.yuseob.chaenggimdol.location.FakeLocationSessionController
import com.yuseob.chaenggimdol.ui.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProcessRecoveryTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun recreatedHomeViewModelRestoresActiveSession() =
        runTest(dispatcher) {
            val sessions = FakeSessionRepository()
            sessions.seed(
                CheckSession(
                    id = 42,
                    startedAtMillis = 1_000,
                    completedAtMillis = null,
                    reminderSent = false,
                    uncheckedCountAtCompletion = null,
                    items = listOf(
                        CheckSessionItem(
                            itemId = 1,
                            name = "휴대폰",
                            status = CheckStatus.Unchecked,
                        ),
                    ),
                ),
            )

            val recreated = HomeViewModel(
                itemRepository = StaticItemRepository(
                    listOf(UserItem(1, "휴대폰", "phone")),
                ),
                sessionRepository = sessions,
                startCheckSession = StartCheckSessionUseCase(sessions),
                locationController =
                    FakeLocationSessionController(),
                canStartTracking = { false },
            )
            advanceUntilIdle()

            assertEquals(
                42L,
                recreated.state.value.activeSessionId,
            )
        }
}

private class StaticItemRepository(
    items: List<UserItem>,
) : ItemRepository {
    private val state = MutableStateFlow(items)

    override fun observeItems(): Flow<List<UserItem>> = state

    override suspend fun upsert(item: UserItem) = Unit

    override suspend fun delete(id: Long) = Unit
}
