//
//  ApperoTheme.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Theme interface for Appero SDK.
 * Implement this interface to create custom themes for the feedback UI.
 *
 * Example:
 * ```kotlin
 * object CustomApperoTheme : ApperoTheme {
 *     override val colors = ApperoColors(
 *         primary = Color(0xFF6200EE),
 *         // ... customize colors
 *     )
 *     override val typography = ApperoTypography()
 *     override val shapes = ApperoShapes()
 * }
 * ```
 */
interface ApperoTheme {
    /**
     * Color palette for the theme.
     * All colors are WCAG 2.2 AA compliant with proper contrast ratios.
     */
    val colors: ApperoColors

    /**
     * Typography scale for text styles.
     * Supports text scaling up to 200%.
     */
    val typography: ApperoTypography

    /**
     * Shape definitions for UI components.
     */
    val shapes: ApperoShapes
}

/**
 * CompositionLocal for accessing the current Appero theme.
 * Use LocalApperoTheme.current to access theme values within composables.
 */
val LOCAL_APPERO_THEME = staticCompositionLocalOf<ApperoTheme> {
    DefaultApperoTheme
}
