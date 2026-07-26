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

private val DarkColorScheme =
  darkColorScheme(
    primary = CuteDarkPrimary,
    secondary = PastelSecondaryLavender,
    tertiary = PastelPeach,
    background = CuteDarkBackground,
    surface = CuteDarkSurface,
    onPrimary = Color.White,
    onSecondary = SoftTextDark,
    onBackground = CuteDarkText,
    onSurface = CuteDarkText
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PastelPinkPrimary,
    secondary = PastelSecondaryLavender,
    tertiary = PastelPeach,
    background = PastelPinkBackground,
    surface = PastelPinkSurface,
    onPrimary = Color.White,
    onSecondary = SoftTextDark,
    onBackground = SoftTextDark,
    onSurface = SoftTextDark,
    outlineVariant = PastelCardOutline
  )

@Composable
fun MyLilluTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep cute theme consistent across Android versions
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
