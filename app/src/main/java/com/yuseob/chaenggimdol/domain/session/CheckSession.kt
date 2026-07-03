package com.yuseob.chaenggimdol.domain.session

data class CheckSession(
    val id: Long,
    val startedAtMillis: Long,
    val completedAtMillis: Long?,
    val reminderSent: Boolean,
    val uncheckedCountAtCompletion: Int?,
    val items: List<CheckSessionItem>,
)

data class CheckSessionItem(
    val itemId: Long,
    val name: String,
    val status: CheckStatus,
)
