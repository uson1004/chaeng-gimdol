package com.yuseob.chaenggimdol.domain.session

class CompleteCheckSessionUseCase(
    private val repository: SessionRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend operator fun invoke(sessionId: Long) {
        val session = requireNotNull(repository.get(sessionId))
        val unchecked = session.items.count {
            it.status == CheckStatus.Unchecked
        }
        repository.complete(
            sessionId = sessionId,
            completedAtMillis = clock(),
            uncheckedCount = unchecked,
        )
    }
}
