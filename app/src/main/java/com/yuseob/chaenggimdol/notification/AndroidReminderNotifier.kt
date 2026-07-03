package com.yuseob.chaenggimdol.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.yuseob.chaenggimdol.MainActivity
import com.yuseob.chaenggimdol.location.LocationTrackingService

class AndroidReminderNotifier(
    private val context: Context,
) : ReminderNotifier {
    override fun showLeavingReminder(
        sessionId: Long,
        uncheckedItemCount: Int,
    ) {
        val openSession = Intent(
            context,
            MainActivity::class.java,
        ).putExtra(
            LocationTrackingService.EXTRA_SESSION_ID,
            sessionId,
        )
        val pendingIntent = PendingIntent.getActivity(
            context,
            sessionId.toInt(),
            openSession,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(
            context,
            REMINDER_CHANNEL_ID,
        ).setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("잠깐, 놓고 가는 물건 없나요?")
            .setContentText(
                "이동한 것으로 보여요. " +
                    "${uncheckedItemCount}개 물건을 확인해 주세요.",
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(REMINDER_NOTIFICATION_ID, notification)
    }
}
