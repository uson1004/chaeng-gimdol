package com.yuseob.chaenggimdol.ui.items

import com.yuseob.chaenggimdol.domain.item.ItemRepository
import com.yuseob.chaenggimdol.domain.item.UserItem
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
class ItemsViewModelTest {
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
    fun blankNameIsRejectedWithoutRepositoryWrite() = runTest(dispatcher) {
        val repository = RecordingItemRepository()
        val viewModel = ItemsViewModel(repository)

        viewModel.setEditorName("   ")
        viewModel.saveEditor()
        advanceUntilIdle()

        assertEquals(
            "물건 이름을 입력해 주세요.",
            viewModel.state.value.errorMessage,
        )
        assertEquals(0, repository.writeCount)
    }

    @Test
    fun failedSaveKeepsInputAndCanBeRetried() = runTest(dispatcher) {
        val repository = FailingThenSuccessfulItemRepository()
        val viewModel = ItemsViewModel(repository)

        viewModel.setEditorName("우산")
        viewModel.saveEditor()
        advanceUntilIdle()

        assertEquals("우산", viewModel.state.value.editorName)
        assertEquals(true, viewModel.state.value.retryAvailable)

        repository.shouldFail = false
        viewModel.retrySave()
        advanceUntilIdle()

        assertEquals("", viewModel.state.value.editorName)
        assertEquals(1, repository.savedItems.size)
    }

    @Test
    fun duplicateNameIsRejectedBeforeRepositoryWrite() = runTest(dispatcher) {
        val repository = RecordingItemRepository(
            initial = listOf(UserItem(1, "휴대폰", "phone")),
        )
        val viewModel = ItemsViewModel(repository)
        advanceUntilIdle()

        viewModel.setEditorName(" 휴대폰 ")
        viewModel.saveEditor()
        advanceUntilIdle()

        assertEquals(
            "이미 등록된 물건이에요.",
            viewModel.state.value.errorMessage,
        )
        assertEquals(0, repository.writeCount)
    }

    @Test
    fun successfulSaveResetsEditorImportance() = runTest(dispatcher) {
        val repository = FailingThenSuccessfulItemRepository().apply {
            shouldFail = false
        }
        val viewModel = ItemsViewModel(repository)

        viewModel.setEditorName("우산")
        viewModel.setEditorImportant(false)
        viewModel.saveEditor()
        advanceUntilIdle()

        assertEquals(true, viewModel.state.value.editorImportant)
    }
}

private class RecordingItemRepository(
    initial: List<UserItem> = emptyList(),
) : ItemRepository {
    private val items = MutableStateFlow(initial)
    var writeCount = 0

    override fun observeItems(): Flow<List<UserItem>> = items

    override suspend fun upsert(item: UserItem) {
        writeCount += 1
    }

    override suspend fun delete(id: Long) = Unit
}

private class FailingThenSuccessfulItemRepository : ItemRepository {
    private val items = MutableStateFlow<List<UserItem>>(emptyList())
    var shouldFail = true
    val savedItems = mutableListOf<UserItem>()

    override fun observeItems(): Flow<List<UserItem>> = items

    override suspend fun upsert(item: UserItem) {
        if (shouldFail) error("storage unavailable")
        savedItems += item
        items.value = savedItems.toList()
    }

    override suspend fun delete(id: Long) = Unit
}
