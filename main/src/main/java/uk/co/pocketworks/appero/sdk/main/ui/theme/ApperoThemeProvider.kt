//
//  ApperoThemeProvider.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Provides Appero theme to composable content.
 *
 * Use this composable to apply a custom theme to the Appero feedback UI.
 * If no theme is provided, the default Material 3-based theme will be used.
 *
 * Example usage:
 * ```kotlin
 * ApperoThemeProvider(theme = CustomApperoTheme) {
 *     ApperoFeedbackUI()
 * }
 * ```
 *
 * @param theme The theme to apply (defaults to DefaultApperoTheme)
 * @param content The composable content to theme
 */
@Composable
fun ApperoThemeProvider(
    theme: ApperoTheme = DefaultApperoTheme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalApperoTheme provides theme,
        content = content
    )
}
