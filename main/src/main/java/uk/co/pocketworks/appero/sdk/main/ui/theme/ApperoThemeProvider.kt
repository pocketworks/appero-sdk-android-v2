//
//  ApperoThemeProvider.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Provides Appero theme to composable content.
 *
 * Use this composable to apply a custom theme to the Appero feedback UI.
 * If no theme is provided, the default Appero Light or Dark mode themes will be used.
 *
 * Example usage:
 * ```kotlin
 * ApperoThemeProvider(theme = CustomApperoTheme) {
 *     ApperoFeedbackUI()
 * }
 * ```
 *
 * @param customTheme The theme to apply
 * @param content The composable content to theme
 */
@Composable
fun ApperoThemeProvider(
    customTheme: ApperoTheme? = null,
    content: @Composable () -> Unit,
) {
    val theme = customTheme ?: when (isSystemInDarkTheme()) {
        true -> DarkApperoTheme
        false -> LightApperoTheme
    }

    CompositionLocalProvider(
        localApperoTheme provides theme,
        content = content
    )
}
