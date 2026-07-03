package com.yuseob.chaenggimdol.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserItemDao {
    @Query("SELECT * FROM user_items ORDER BY important DESC, id ASC")
    fun observeAll(): Flow<List<UserItemEntity>>

    @Upsert
    suspend fun upsert(item: UserItemEntity): Long

    @Query("DELETE FROM user_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
