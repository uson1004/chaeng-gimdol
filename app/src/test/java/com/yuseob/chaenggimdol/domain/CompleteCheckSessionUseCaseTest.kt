package com.yuseob.chaenggimdol.domain

import com.yuseob.chaenggimdol.domain.session.CompleteCheckSessionUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CompleteCheckSessionUseCaseTest {
    @Test
    fun completionPreservesUncheckedCount() = runTest {
        val repository = sessionRepositoryWithUncheckedItems(2)

        CompleteCheckSessionUseCase(
            repository = repository,
            clock = { 5_000L },
        )(sessionId = 1)

        assertEquals(
            2,
            repository.requireSession(1).uncheckedCountAtCompletion,
        )
    }
}
