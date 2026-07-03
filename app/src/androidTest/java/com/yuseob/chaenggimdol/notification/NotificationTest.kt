package com.yuseob.chaenggimdol.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class NotificationTest {
    private val context =
        ApplicationProvider.getApplicationContext<Context>()
    private val manager =
        context.getSystemService(NotificationManager::class.java)

    @After
    fun cleanUp() {
        manager.cancel(REMINDER_NOTIFICATION_ID)
    }

    @Test
    fun leavingReminderPostsNotification() {
        grantRuntimePermission(Manifest.permission.POST_NOTIFICATIONS)
        createNotificationChannels(context)
        manager.cancel(REMINDER_NOTIFICATION_ID)

        AndroidReminderNotifier(context).showLeavingReminder(
            sessionId = 42L,
            uncheckedItemCount = 3,
        )

        val notification = waitForNotification(REMINDER_NOTIFICATION_ID)
            .notification
        assertEquals(
            "잠깐, 놓고 가는 물건 없나요?",
            notification.extras.getCharSequence(
                Notification.EXTRA_TITLE,
            ).toString(),
        )
        assertEquals(
            "이동한 것으로 보여요. 3개 물건을 확인해 주세요.",
            notification.extras.getCharSequence(
                Notification.EXTRA_TEXT,
            ).toString(),
        )
    }

    private fun waitForNotification(id: Int) =
        (1..20).firstNotNullOfOrNull {
            manager.activeNotifications.firstOrNull {
                it.id == id && it.packageName == context.packageName
            } ?: run {
                Thread.sleep(100)
                null
            }
        }.also {
            assertNotNull(it)
        }!!

    private fun grantRuntimePermission(permission: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        runCatching {
            InstrumentationRegistry.getInstrumentation()
                .uiAutomation
                .grantRuntimePermission(context.packageName, permission)
        }
    }
}
