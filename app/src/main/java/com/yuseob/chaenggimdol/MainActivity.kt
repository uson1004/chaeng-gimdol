package com.yuseob.chaenggimdol

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yuseob.chaenggimdol.location.LocationTrackingService
import com.yuseob.chaenggimdol.navigation.AppNavigation
import com.yuseob.chaenggimdol.ui.theme.ChaenggimDolTheme
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    private val notificationSessionId = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationSessionId.value = intent.notificationSessionId()
        val container =
            (application as ChaenggimDolApplication).container
        setContent {
            val onboardingFlow = remember(container) {
                container.preferences.isOnboardingComplete
                    .map<Boolean, Boolean?> { it }
            }
            val onboardingComplete by onboardingFlow
                .collectAsStateWithLifecycle(initialValue = null)
            onboardingComplete?.let { complete ->
                ChaenggimDolTheme {
                    AppNavigation(
                        container = container,
                        onboardingComplete = complete,
                        notificationSessionId =
                            notificationSessionId.value,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notificationSessionId.value = intent.notificationSessionId()
    }
}

private fun Intent?.notificationSessionId(): Long? =
    this?.getLongExtra(
        LocationTrackingService.EXTRA_SESSION_ID,
        0L,
    )?.takeIf { it > 0L }
