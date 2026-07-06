package com.yuseob.chaenggimdol.domain

import com.yuseob.chaenggimdol.domain.item.UserItem
import com.yuseob.chaenggimdol.domain.session.CheckSession
import com.yuseob.chaenggimdol.domain.session.CheckSessionItem
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.domain.session.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSessionRepository : SessionRepository {
    private var nextId = 1L
    private val sessions = linkedMapOf<Long, CheckSession>()
    private val active = MutableStateFlow<CheckSession?>(null)

    override suspend fun create(
        items: List<UserItem>,
        startedAtMillis: Long,
    ): Long {
        val id = nextId++
        val session = CheckSession(
            id = id,
            startedAtMillis = startedAtMillis,
            completedAtMillis = null,
            reminderSent = false,
            uncheckedCountAtCompletion = null,
            items = items.map {
                CheckSessionItem(
                    itemId = it.id,
                    name = it.name,
                    status = CheckStatus.Unchecked,
                    important = it.important,
                    checkHint = it.checkHint,
                )
            },
        )
        sessions[id] = session
        active.value = session
        return id
    }

    override fun observeActive(): Flow<CheckSession?> = active

    override suspend fun setItemStatus(
        sessionId: Long,
        itemId: Long,
        status: CheckStatus,
    ) {
        update(sessionId) { session ->
            session.copy(
                items = session.items.map { item ->
                    if (item.itemId == itemId) item.copy(status = status) else item
                },
            )
        }
    }

    override suspend fun markReminderSent(sessionId: Long) {
        update(sessionId) { it.copy(reminderSent = true) }
    }

    override suspend fun complete(
        sessionId: Long,
        completedAtMillis: Long,
        uncheckedCount: Int,
    ) {
        update(sessionId) {
            it.copy(
                completedAtMillis = completedAtMillis,
                uncheckedCountAtCompletion = uncheckedCount,
            )
        }
        active.value = null
    }

    override suspend fun get(sessionId: Long): CheckSession? = sessions[sessionId]

    fun requireSession(id: Long): CheckSession = requireNotNull(sessions[id])

    suspend fun seed(session: CheckSession) {
        sessions[session.id] = session
        if (session.completedAtMillis == null) {
            active.value = session
        }
        nextId = maxOf(nextId, session.id + 1)
    }

    private fun update(
        id: Long,
        transform: (CheckSession) -> CheckSession,
    ) {
        val updated = transform(requireSession(id))
        sessions[id] = updated
        if (active.value?.id == id) {
            active.value = updated
        }
    }
}

suspend fun sessionRepositoryWithUncheckedItems(
    count: Int,
): FakeSessionRepository {
    val repository = FakeSessionRepository()
    repository.seed(
        CheckSession(
            id = 1,
            startedAtMillis = 1_000L,
            completedAtMillis = null,
            reminderSent = false,
            uncheckedCountAtCompletion = null,
            items = (1..count).map {
                CheckSessionItem(
                    itemId = it.toLong(),
                    name = "물건 $it",
                    status = CheckStatus.Unchecked,
                )
            },
        ),
    )
    return repository
}
