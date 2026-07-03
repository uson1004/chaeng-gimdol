package com.yuseob.chaenggimdol.data.local

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class SessionWithItems(
    @Embedded
    val session: SessionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId",
    )
    val items: List<SessionItemEntity>,
)

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: SessionEntity): Long

    @Insert
    suspend fun insertItems(items: List<SessionItemEntity>)

    @Transaction
    @Query(
        """
        SELECT * FROM check_sessions
        WHERE completedAtMillis IS NULL
        ORDER BY startedAtMillis DESC
        LIMIT 1
        """,
    )
    fun observeActive(): Flow<SessionWithItems?>

    @Transaction
    @Query("SELECT * FROM check_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun get(sessionId: Long): SessionWithItems?

    @Query(
        """
        UPDATE session_items
        SET status = :status
        WHERE sessionId = :sessionId AND itemId = :itemId
        """,
    )
    suspend fun updateItemStatus(
        sessionId: Long,
        itemId: Long,
        status: String,
    )

    @Query(
        "UPDATE check_sessions SET reminderSent = 1 WHERE id = :sessionId",
    )
    suspend fun markReminderSent(sessionId: Long)

    @Query(
        """
        UPDATE check_sessions
        SET completedAtMillis = :completedAtMillis,
            uncheckedCountAtCompletion = :uncheckedCount
        WHERE id = :sessionId
        """,
    )
    suspend fun complete(
        sessionId: Long,
        completedAtMillis: Long,
        uncheckedCount: Int,
    )
}
