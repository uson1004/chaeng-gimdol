package com.yuseob.chaenggimdol.domain

import com.yuseob.chaenggimdol.domain.item.ItemRepository
import com.yuseob.chaenggimdol.domain.item.SeedDefaultItemsUseCase
import com.yuseob.chaenggimdol.domain.item.UserItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SeedDefaultItemsUseCaseTest {
    @Test
    fun selectedTemplatesAreSavedOnce() = runTest {
        val repository = FakeItemRepository()

        SeedDefaultItemsUseCase(repository)(
            setOf("휴대폰", "이어폰", "충전기"),
        )

        assertEquals(
            listOf("휴대폰", "이어폰", "충전기"),
            repository.saved.map { it.name },
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
