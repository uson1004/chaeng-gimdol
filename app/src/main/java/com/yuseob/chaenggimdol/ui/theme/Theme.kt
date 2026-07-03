package com.yuseob.chaenggimdol.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SignalBuddyColors = lightColorScheme(
    primary = SignalLime,
    onPrimary = Charcoal,
    secondary = DeepLilac,
    onSecondary = SignalSurface,
    tertiary = BuddyCoral,
    background = WarmIvory,
    onBackground = Charcoal,
    surface = SignalSurface,
    onSurface = Charcoal,
    error = ErrorRed,
)

@Composable
fun ChaenggimDolTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SignalBuddyColors,
        typography = AppTypography,
        content = content,
    )
}
