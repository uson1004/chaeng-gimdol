package com.yuseob.chaenggimdol.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

const val TRACKING_CHANNEL_ID = "tracking"
const val REMINDER_CHANNEL_ID = "reminders"
const val TRACKING_NOTIFICATION_ID = 1001
const val REMINDER_NOTIFICATION_ID = 1002

fun createNotificationChannels(context: Context) {
    val manager = context.getSystemService(
        NotificationManager::class.java,
    )
    manager.createNotificationChannels(
        listOf(
            NotificationChannel(
                TRACKING_CHANNEL_ID,
                "세션 추적",
                NotificationManager.IMPORTANCE_LOW,
            ),
            NotificationChannel(
                REMINDER_CHANNEL_ID,
                "챙김 알림",
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        ),
    )
}
