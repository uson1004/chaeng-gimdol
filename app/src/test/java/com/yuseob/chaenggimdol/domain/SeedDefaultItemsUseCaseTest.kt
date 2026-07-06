package com.yuseob.chaenggimdol.domain

import com.yuseob.chaenggimdol.domain.item.ItemRepository
import com.yuseob.chaenggimdol.domain.item.SeedItem
import com.yuseob.chaenggimdol.domain.item.SeedDefaultItemsUseCase
import com.yuseob.chaenggimdol.domain.item.UserItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SeedDefaultItemsUseCaseTest {
    @Test
    fun selectedTemplatesAreSavedWithCheckMetadata() = runTest {
        val repository = FakeItemRepository()

        SeedDefaultItemsUseCase(repository)(
            listOf(
                SeedItem("휴대폰", important = true, checkHint = "가방 안"),
                SeedItem("우산", important = false, checkHint = "  "),
            ),
        )

        assertEquals(
            listOf(
                UserItem(
                    name = "휴대폰",
                    category = "phone",
                    important = true,
                    checkHint = "가방 안",
                ),
                UserItem(
                    name = "우산",
                    category = "umbrella",
                    important = false,
                    checkHint = null,
                ),
            ),
            repository.saved,
        )
    }
}

private class FakeItemRepository : ItemRepository {
    private val items = MutableStateFlow<List<UserItem>>(emptyList())
    val saved = mutableListOf<UserItem>()

    override fun observeItems(): Flow<List<UserItem>> = items

    override suspend fun upsert(item: UserItem) {
        saved.removeAll { it.id != 0L && it.id == item.id }
        saved += item
        items.value = saved.toList()
    }

    override suspend fun delete(id: Long) {
        saved.removeAll { it.id == id }
        items.value = saved.toList()
    }
}
