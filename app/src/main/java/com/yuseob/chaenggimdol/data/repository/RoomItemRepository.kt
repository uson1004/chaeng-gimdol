package com.yuseob.chaenggimdol.data.repository

import com.yuseob.chaenggimdol.data.local.UserItemDao
import com.yuseob.chaenggimdol.data.local.UserItemEntity
import com.yuseob.chaenggimdol.domain.item.ItemRepository
import com.yuseob.chaenggimdol.domain.item.UserItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomItemRepository(
    private val dao: UserItemDao,
) : ItemRepository {
    override fun observeItems(): Flow<List<UserItem>> =
        dao.observeAll().map { items ->
            items.map(UserItemEntity::toDomain)
        }

    override suspend fun upsert(item: UserItem) {
        dao.upsert(item.toEntity())
    }

    override suspend fun delete(id: Long) {
        dao.deleteById(id)
    }
}

private fun UserItemEntity.toDomain() = UserItem(
    id = id,
    name = name,
    category = category,
    important = important,
    active = active,
)

private fun UserItem.toEntity() = UserItemEntity(
    id = id,
    name = name,
    category = category,
    important = important,
    active = active,
)
