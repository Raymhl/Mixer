package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DlmsColorScheme =
  darkColorScheme(
    primary = CyanPrimary,
    onPrimary = BackgroundDark,
    primaryContainer = CyanPrimaryDark,
    onPrimaryContainer = TextPrimary,
    secondary = MagentaAccent,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    tertiary = GreenSuccess,
    onTertiary = BackgroundDark
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for DLMS
  dynamicColor: Boolean = false, // Force custom colors
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DlmsColorScheme, typography = Typography, content = content)
}
