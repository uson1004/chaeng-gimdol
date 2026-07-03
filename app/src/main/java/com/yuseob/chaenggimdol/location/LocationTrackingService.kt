package com.yuseob.chaenggimdol.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.yuseob.chaenggimdol.ChaenggimDolApplication
import com.yuseob.chaenggimdol.MainActivity
import com.yuseob.chaenggimdol.domain.session.CheckStatus
import com.yuseob.chaenggimdol.notification.TRACKING_CHANNEL_ID
import com.yuseob.chaenggimdol.notification.TRACKING_NOTIFICATION_ID
import com.yuseob.chaenggimdol.notification.createNotificationChannels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationTrackingService : Service() {
    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main.immediate,
    )
    private lateinit var locationClient: FusedLocationProviderClient
    private val container
        get() = (application as ChaenggimDolApplication).container

    private var startLocation: Location? = null
    private var startedElapsedRealtime = 0L
    private var sessionId = 0L
    private var reminderSent = false
    private var trackingActive = false

    private val policy = ExitDetectionPolicy(
        minimumDurationMillis = 10 * 60 * 1_000L,
        exitRadiusMeters = 100f,
        maximumAccuracyMeters = 50f,
    )

    private val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val current = result.lastLocation ?: return
            val start = startLocation ?: run {
                if (current.accuracy <= MAXIMUM_ACCURACY_METERS) {
                    startLocation = current
                }
                return
            }
            if (reminderSent) {
                return
            }

            if (
                policy.shouldNotify(
                    elapsedMillis = SystemClock.elapsedRealtime() -
                        startedElapsedRealtime,
                    distanceMeters = start.distanceTo(current),
                    accuracyMeters = current.accuracy,
                )
            ) {
                notifyOnce()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels(this)
        locationClient = LocationServices
            .getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        when (intent?.action) {
            ACTION_STOP -> stopTracking()
            ACTION_START -> {
                val requestedSessionId = intent.getLongExtra(
                    EXTRA_SESSION_ID,
                    0L,
                )
                if (requestedSessionId <= 0L) {
                    stopSelf()
                    return START_NOT_STICKY
                }
                sessionId = requestedSessionId
                if (trackingActive) {
                    locationClient.removeLocationUpdates(callback)
                    trackingActive = false
                }
                reminderSent = false
                startLocation = null
                startedElapsedRealtime = SystemClock.elapsedRealtime()
                startForeground(
                    TRACKING_NOTIFICATION_ID,
                    trackingNotification(),
                )
                startTracking()
            }
        }
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        if (!hasLocationPermission()) {
            stopTracking()
            return
        }

        try {
            locationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token,
            ).addOnSuccessListener { location ->
                if (
                    location != null &&
                    location.accuracy <= MAXIMUM_ACCURACY_METERS
                ) {
                    startLocation = location
                }
            }

            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                UPDATE_INTERVAL_MILLIS,
            ).setMinUpdateIntervalMillis(
                MINIMUM_UPDATE_INTERVAL_MILLIS,
            ).build()

            locationClient.requestLocationUpdates(
                request,
                callback,
                mainLooper,
            )
            trackingActive = true
        } catch (_: SecurityException) {
            stopTracking()
        }
    }

    private fun hasLocationPermission(): Boolean {
        val coarse = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val fine = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        return coarse || fine
    }

    private fun notifyOnce() {
        reminderSent = true
        scope.launch {
            val session = container.sessionRepository.get(sessionId)
                ?: return@launch
            if (
                session.reminderSent ||
                session.completedAtMillis != null
            ) {
                return@launch
            }
            val unchecked = session.items.count {
                it.status == CheckStatus.Unchecked
            }
            container.reminderNotifier.showLeavingReminder(
                sessionId = sessionId,
                uncheckedItemCount = unchecked,
            )
            container.sessionRepository.markReminderSent(sessionId)
            stopTracking()
        }
    }

    private fun trackingNotification(): Notification {
        val intent = Intent(
            this,
            MainActivity::class.java,
        ).putExtra(EXTRA_SESSION_ID, sessionId)
        val pendingIntent = PendingIntent.getActivity(
            this,
            sessionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(
            this,
            TRACKING_CHANNEL_ID,
        ).setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("챙김 모드가 켜져 있어요")
            .setContentText(
                "이동을 감지하는 동안 수동 체크도 계속 사용할 수 있어요.",
            )
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun stopTracking() {
        if (::locationClient.isInitialized && trackingActive) {
            locationClient.removeLocationUpdates(callback)
        }
        trackingActive = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        if (::locationClient.isInitialized && trackingActive) {
            locationClient.removeLocationUpdates(callback)
        }
        trackingActive = false
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START =
            "com.yuseob.chaenggimdol.action.START_TRACKING"
        const val ACTION_STOP =
            "com.yuseob.chaenggimdol.action.STOP_TRACKING"
        const val EXTRA_SESSION_ID = "session_id"

        private const val UPDATE_INTERVAL_MILLIS = 15_000L
        private const val MINIMUM_UPDATE_INTERVAL_MILLIS = 10_000L
        private const val MAXIMUM_ACCURACY_METERS = 50f
    }
}
