package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = RedPrimary,
    onPrimary = WarmSurface,
    secondary = WarmBg,
    onSecondary = CharcoalText,
    tertiary = PeachLight,
    background = Color(0xFF121211),
    surface = Color(0xFF1E1E1C),
    onBackground = WarmBg,
    onSurface = WarmBg,
    primaryContainer = Color(0xFF3D0100),
    onPrimaryContainer = PeachLight,
    surfaceVariant = Color(0xFF292927),
    onSurfaceVariant = WarmBg,
    outline = Color(0xFF3E3E3C)
  )

private val LightColorScheme =
  lightColorScheme(
    primary = RedPrimary,
    onPrimary = WarmSurface,
    secondary = CharcoalText,
    onSecondary = WarmBg,
    tertiary = RedDark,
    background = WarmBg,
    surface = WarmSurface,
    onBackground = CharcoalText,
    onSurface = CharcoalText,
    primaryContainer = PeachLight,
    onPrimaryContainer = RedDark,
    surfaceVariant = CreamLight,
    onSurfaceVariant = CharcoalText,
    outline = GrayBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
