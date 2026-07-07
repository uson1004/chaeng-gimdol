package com.yuseob.chaenggimdol.data

import com.yuseob.chaenggimdol.data.local.UserItemDao
import com.yuseob.chaenggimdol.data.local.UserItemEntity
import com.yuseob.chaenggimdol.data.repository.RoomItemRepository
import com.yuseob.chaenggimdol.domain.item.UserItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RoomItemRepositoryTest {
    @Test
    fun upsertStoresNormalizedNameForUniqueness() = runTest {
        val dao = RecordingUserItemDao()

        RoomItemRepository(dao).upsert(
            UserItem(name = "  휴대폰  ", category = "phone"),
        )

        assertEquals("휴대폰", dao.lastItem!!.name)
        assertEquals("휴대폰", dao.lastItem!!.normalizedName)
    }
}

private class RecordingUserItemDao : UserItemDao {
    private val items = MutableStateFlow<List<UserItemEntity>>(emptyList())
    var lastItem: UserItemEntity? = null

    override fun observeAll(): Flow<List<UserItemEntity>> = items

    override suspend fun upsert(item: UserItemEntity): Long {
        lastItem = item
        return 1L
    }

    override suspend fun deleteById(id: Long) = Unit
}
