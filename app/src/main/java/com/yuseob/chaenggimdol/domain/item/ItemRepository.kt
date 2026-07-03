package com.yuseob.chaenggimdol.domain.item

import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun observeItems(): Flow<List<UserItem>>

    suspend fun upsert(item: UserItem)

    suspend fun delete(id: Long)
}
