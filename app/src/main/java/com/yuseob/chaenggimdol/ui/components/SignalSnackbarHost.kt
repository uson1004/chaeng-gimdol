package com.yuseob.chaenggimdol.ui.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.yuseob.chaenggimdol.ui.theme.Charcoal
import com.yuseob.chaenggimdol.ui.theme.SignalLime
import com.yuseob.chaenggimdol.ui.theme.SignalSurface

@Composable
fun SignalSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
            .testTag("signal-snackbar-host")
            .semantics {
                liveRegion = LiveRegionMode.Polite
            },
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = Charcoal,
            contentColor = SignalSurface,
            actionColor = SignalLime,
        )
    }
}
