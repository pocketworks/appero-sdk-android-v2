//
//  Theme.kt
//  Appero SDK Sample
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Light color scheme matching values/themes.xml
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF03DAC5),
    onSecondary = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000)
)

/**
 * Dark color scheme matching values-night/themes.xml
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    secondary = Color(0xFF03DAC5),
    onSecondary = Color(0xFF000000),
    surface = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF)
)

/**
 * Sample app theme that automatically follows system light/dark mode.
 *
 * This theme is used for the main app UI and respects the device's
 * system theme setting. The Appero feedback UI uses separate custom
 * themes that can be selected independently.
 */
@Composable
fun SampleAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
