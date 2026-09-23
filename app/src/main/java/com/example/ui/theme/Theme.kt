package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.editor.settings.ThemeMode

val BlackColorScheme =
    darkColorScheme(
        primary = IdePrimaryBlack,
        onPrimary = Color(0xFF000000),
        primaryContainer = Color(0xFF0D2D57),
        onPrimaryContainer = Color(0xFFCCE2FF),
        secondary = IdeSecondaryBlack,
        onSecondary = Color(0xFF000000),
        secondaryContainer = Color(0xFF13361E),
        onSecondaryContainer = Color(0xFF7EE787),
        tertiary = IdeTertiaryBlack,
        onTertiary = Color(0xFF000000),
        background = IdeBackgroundBlack, // 0xFF000000
        onBackground = Color(0xFFF0F6FC),
        surface = IdeSurfaceBlack,       // 0xFF000000
        onSurface = Color(0xFFF0F6FC),
        surfaceVariant = Color(0xFF101010),
        onSurfaceVariant = Color(0xFFADB5BD),
        surfaceContainerLowest = Color(0xFF000000),
        surfaceContainerLow = Color(0xFF050505),
        surfaceContainer = IdeSurfaceContainerBlack,        // 0xFF0A0A0A
        surfaceContainerHigh = IdeSurfaceContainerHighBlack,    // 0xFF141414
        surfaceContainerHighest = IdeSurfaceContainerHighestBlack, // 0xFF1E1E1E
        outline = Color(0xFF333333),
        outlineVariant = Color(0xFF222222)
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = IdePrimaryDark,
        secondary = IdeSecondaryDark,
        tertiary = IdeTertiaryDark,
        background = IdeBackgroundDark,
        surface = IdeSurfaceDark,
        surfaceContainer = IdeSurfaceContainerDark,
        surfaceContainerHigh = IdeSurfaceContainerHighDark
    )

private val LightColorScheme =
    lightColorScheme(
        primary = IdePrimaryLight,
        secondary = IdeSecondaryLight,
        tertiary = IdeTertiaryLight,
        background = IdeBackgroundLight,
        surface = IdeSurfaceLight,
        surfaceContainer = IdeSurfaceContainerLight,
        surfaceContainerHigh = IdeSurfaceContainerHighLight
    )

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.BLACK,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (themeMode) {
        ThemeMode.BLACK -> BlackColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.SYSTEM -> {
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else if (darkTheme) {
                DarkColorScheme
            } else {
                LightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
