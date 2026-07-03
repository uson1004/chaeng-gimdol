package com.yuseob.chaenggimdol.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SignalBuddyColors = lightColorScheme(
    primary = SignalLime,
    onPrimary = Charcoal,
    primaryContainer = SignalLime,
    onPrimaryContainer = Charcoal,
    secondary = DeepLilac,
    onSecondary = SignalSurface,
    secondaryContainer = LilacContainer,
    onSecondaryContainer = DeepLilac,
    tertiary = BuddyCoral,
    onTertiary = Charcoal,
    tertiaryContainer = CoralContainer,
    onTertiaryContainer = Charcoal,
    background = WarmIvory,
    onBackground = Charcoal,
    surface = SignalSurface,
    onSurface = Charcoal,
    surfaceVariant = IvorySurfaceVariant,
    onSurfaceVariant = Charcoal,
    outline = IvoryOutline,
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
