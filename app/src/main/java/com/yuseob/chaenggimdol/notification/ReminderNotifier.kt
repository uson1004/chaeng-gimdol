package com.yuseob.chaenggimdol.notification

interface ReminderNotifier {
    fun showLeavingReminder(
        sessionId: Long,
        uncheckedItemCount: Int,
    )
}
