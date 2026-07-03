package com.yuseob.chaenggimdol.data.repository

import androidx.room.withTransaction
import com.yuseob.chaenggimdol.data.local.ChaenggimDatabase
import com.yuseob.chaenggimdol.data.local.SessionEntity
import com.yuseob.chaenggimdol.data.local.SessionItemEntity
import com.yuseob.chaenggimdol.data.local.SessionWithItems
import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.domain.session.CheckSession
import com.yuseob.chaenggimdol.domain.session.CheckSessionItem
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.domain.session.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomSessionRepository(
    private val database: ChaenggimDatabase,
) : SessionRepository {
    private val dao = database.sessionDao()

    override suspend fun create(
        items: List<UserItem>,
        startedAtMillis: Long,
    ): Long = database.withTransaction {
        val sessionId = dao.insertSession(
            SessionEntity(startedAtMillis = startedAtMillis),
        )
        dao.insertItems(
            items.map { item ->
                SessionItemEntity(
                    sessionId = sessionId,
                    itemId = item.id,
                    name = item.name,
                    status = CheckStatus.Unchecked.name,
                )
            },
        )
        sessionId
    }

    override fun observeActive(): Flow<CheckSession?> =
        dao.observeActive().map { relation ->
            relation?.toDomain()
        }

    override suspend fun setItemStatus(
        sessionId: Long,
        itemId: Long,
        status: CheckStatus,
    ) {
        dao.updateItemStatus(sessionId, itemId, status.name)
    }

    override suspend fun markReminderSent(sessionId: Long) {
        dao.markReminderSent(sessionId)
    }

    override suspend fun complete(
        sessionId: Long,
        completedAtMillis: Long,
        uncheckedCount: Int,
    ) {
        dao.complete(sessionId, completedAtMillis, uncheckedCount)
    }

    override suspend fun get(sessionId: Long): CheckSession? =
        dao.get(sessionId)?.toDomain()
}

private fun SessionWithItems.toDomain() = CheckSession(
    id = session.id,
    startedAtMillis = session.startedAtMillis,
    completedAtMillis = session.completedAtMillis,
    reminderSent = session.reminderSent,
    uncheckedCountAtCompletion = session.uncheckedCountAtCompletion,
    items = items.map { item ->
        CheckSessionItem(
            itemId = item.itemId,
            name = item.name,
            status = CheckStatus.valueOf(item.status),
        )
    },
)
