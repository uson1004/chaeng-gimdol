package com.yuseob.chaenggimdol.domain.session

import com.yuseob.chaenggimdol.domain.item.UserItem
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun create(
        items: List<UserItem>,
        startedAtMillis: Long,
    ): Long

    fun observeActive(): Flow<CheckSession?>

    suspend fun setItemStatus(
        sessionId: Long,
        itemId: Long,
        status: CheckStatus,
    )

    suspend fun markReminderSent(sessionId: Long)

    suspend fun complete(
        sessionId: Long,
        completedAtMillis: Long,
        uncheckedCount: Int,
    )

    suspend fun get(sessionId: Long): CheckSession?
}
