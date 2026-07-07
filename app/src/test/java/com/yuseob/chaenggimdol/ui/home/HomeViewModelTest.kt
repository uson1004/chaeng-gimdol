package com.yuseob.chaenggimdol.ui.home

import com.yuseob.chaenggimdol.domain.FakeSessionRepository
import com.yuseob.chaenggimdol.domain.item.ItemRepository
import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.domain.session.StartCheckSessionUseCase
import com.yuseob.chaenggimdol.location.FakeLocationSessionController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
class HomeViewModelTest {
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
    fun noItemsShowRegistrationMessage() = runTest(dispatcher) {
        val sessions = FakeSessionRepository()
        val viewModel = HomeViewModel(
            itemRepository = FakeItemRepository(emptyList()),
            sessionRepository = sessions,
            startCheckSession = StartCheckSessionUseCase(sessions),
            locationController = FakeLocationSessionController(),
            canStartTracking = { false },
        )
        advanceUntilIdle()

        viewModel.startSession()
        advanceUntilIdle()

        assertEquals(
            "먼저 챙길 물건을 등록해 주세요.",
            viewModel.state.value.message,
        )
    }

    @Test
    fun activeItemsCreateSessionAndEmitNavigation() = runTest(dispatcher) {
        val sessions = FakeSessionRepository()
        val viewModel = HomeViewModel(
            itemRepository = FakeItemRepository(
                listOf(UserItem(1, "휴대폰", "phone")),
            ),
            sessionRepository = sessions,
            startCheckSession = StartCheckSessionUseCase(
                sessions,
                clock = { 1_000L },
            ),
            locationController = FakeLocationSessionController(),
            canStartTracking = { false },
        )
        advanceUntilIdle()
        val effect = async { viewModel.effects.first() }

        viewModel.startSession()
        advanceUntilIdle()

        assertEquals(
            1L,
            (effect.await() as HomeEffect.NavigateToSession).sessionId,
        )
    }

    @Test
    fun trackingStartsOnlyWhenEnabledAndPermitted() = runTest(dispatcher) {
        val sessions = FakeSessionRepository()
        val controller = FakeLocationSessionController()
        val viewModel = HomeViewModel(
            itemRepository = FakeItemRepository(
                listOf(UserItem(1, "휴대폰", "phone")),
            ),
            sessionRepository = sessions,
            startCheckSession = StartCheckSessionUseCase(sessions),
            locationController = controller,
            canStartTracking = { true },
        )
        advanceUntilIdle()

        viewModel.startSession()
        advanceUntilIdle()

        assertEquals(listOf(1L), controller.started)
    }

    @Test
    fun activeItemsAreSummarizedByImportance() = runTest(dispatcher) {
        val sessions = FakeSessionRepository()
        val viewModel = HomeViewModel(
            itemRepository = FakeItemRepository(
                listOf(
                    UserItem(1, "휴대폰", "phone", important = true, active = true),
                    UserItem(2, "우산", "umbrella", important = false, active = true),
                    UserItem(3, "여권", "document", important = true, active = false),
                ),
            ),
            sessionRepository = sessions,
            startCheckSession = StartCheckSessionUseCase(sessions),
            locationController = FakeLocationSessionController(),
            canStartTracking = { false },
        )
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.activeImportantCount)
        assertEquals(1, viewModel.state.value.activeOptionalCount)
    }

}

private class FakeItemRepository(
    initial: List<UserItem>,
) : ItemRepository {
    private val items = MutableStateFlow(initial)

    override fun observeItems(): Flow<List<UserItem>> = items

    override suspend fun upsert(item: UserItem) {
        items.value = items.value.filterNot {
            item.id != 0L && it.id == item.id
        } + item
    }

    override suspend fun delete(id: Long) {
        items.value = items.value.filterNot { it.id == id }
    }
}
