package com.yuseob.chaenggimdol.domain.session

import com.yuseob.chaenggimdol.domain.item.UserItem

class StartCheckSessionUseCase(
    private val repository: SessionRepository,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    suspend operator fun invoke(items: List<UserItem>): Long {
        val activeItems = items.filter(UserItem::active)
        require(activeItems.isNotEmpty()) {
            "At least one active item is required"
        }
        return repository.create(activeItems, clock())
    }
}
