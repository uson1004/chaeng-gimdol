package com.yuseob.chaenggimdol.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SignalBuddyColors = lightColorScheme(
    primary = SignalLime,
    onPrimary = Charcoal,
    primaryContainer = SignalLime,
    onPrimaryContainer = Charcoal,
    secondary = PineTeal,
    onSecondary = SignalSurface,
    secondaryContainer = PineTealContainer,
    onSecondaryContainer = PineTeal,
    tertiary = BuddyCoral,
    onTertiary = Charcoal,
    tertiaryContainer = CoralContainer,
    onTertiaryContainer = Charcoal,
    background = SoftSage,
    onBackground = Charcoal,
    surface = SignalSurface,
    onSurface = Charcoal,
    surfaceVariant = SageSurfaceVariant,
    onSurfaceVariant = Charcoal,
    outline = PineTealOutline,
    error = ErrorRed,
    onError = SignalSurface,
)

@Composable
fun ChaenggimDolTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SignalBuddyColors,
        typography = AppTypography,
        content = content,
    )
}
