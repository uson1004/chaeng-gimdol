package com.yuseob.chaenggimdol.location

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AndroidLocationSessionController(
    private val context: Context,
) : LocationSessionController {
    override fun start(sessionId: Long) {
        val intent = Intent(
            context,
            LocationTrackingService::class.java,
        ).setAction(LocationTrackingService.ACTION_START)
            .putExtra(LocationTrackingService.EXTRA_SESSION_ID, sessionId)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun stop() {
        context.startService(
            Intent(
                context,
                LocationTrackingService::class.java,
            ).setAction(LocationTrackingService.ACTION_STOP),
        )
    }
}
