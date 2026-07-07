package com.yuseob.chaenggimdol.ui.session

import com.yuseob.chaenggimdol.domain.sessionRepositoryWithUncheckedItems
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.domain.session.CompleteCheckSessionUseCase
import com.yuseob.chaenggimdol.location.FakeLocationSessionController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class SessionViewModelTest {
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
    fun uncheckedItemCyclesToPackedAndBack() = runTest(dispatcher) {
        val sessions = sessionRepositoryWithUncheckedItems(1)
        val viewModel = SessionViewModel(
            sessionId = 1,
            repository = sessions,
            completeSession = CompleteCheckSessionUseCase(sessions),
            locationController = FakeLocationSessionController(),
        )
        advanceUntilIdle()

        viewModel.togglePacked(1)
        advanceUntilIdle()
        assertEquals(
            CheckStatus.Packed,
            sessions.requireSession(1).items.single().status,
        )

        viewModel.togglePacked(1)
        advanceUntilIdle()
        assertEquals(
            CheckStatus.Unchecked,
            sessions.requireSession(1).items.single().status,
        )
    }

    @Test
    fun incompleteCompletionRequiresConfirmation() = runTest(dispatcher) {
        val sessions = sessionRepositoryWithUncheckedItems(2)
        val viewModel = SessionViewModel(
            sessionId = 1,
            repository = sessions,
            completeSession = CompleteCheckSessionUseCase(sessions),
            locationController = FakeLocationSessionController(),
        )
        advanceUntilIdle()

        viewModel.requestComplete()

        assertEquals(2, viewModel.state.value.confirmIncompleteCount)
    }

    @Test
    fun completionStopsTracking() = runTest(dispatcher) {
        val sessions = sessionRepositoryWithUncheckedItems(0)
        val controller = FakeLocationSessionController()
        val viewModel = SessionViewModel(
            sessionId = 1,
            repository = sessions,
            completeSession = CompleteCheckSessionUseCase(sessions),
            locationController = controller,
        )
        advanceUntilIdle()

        viewModel.completeConfirmed()
        advanceUntilIdle()

        assertEquals(1, controller.stopCount)
    }

    @Test
    fun stateCountsCheckedAndUncheckedItems() {
        val state = SessionUiState(
            sessionId = 1,
            items = listOf(
                com.yuseob.chaenggimdol.domain.session.CheckSessionItem(
                    itemId = 1,
                    name = "휴대폰",
                    status = CheckStatus.Packed,
                ),
                com.yuseob.chaenggimdol.domain.session.CheckSessionItem(
                    itemId = 2,
                    name = "우산",
                    status = CheckStatus.NotApplicable,
                    important = false,
                ),
                com.yuseob.chaenggimdol.domain.session.CheckSessionItem(
                    itemId = 3,
                    name = "지갑",
                    status = CheckStatus.Unchecked,
                ),
            ),
        )

        assertEquals(2, state.checkedCount)
        assertEquals(1, state.uncheckedCount)
        assertEquals(1, state.uncheckedImportantCount)
        assertEquals(0, state.uncheckedOptionalCount)
    }
}
