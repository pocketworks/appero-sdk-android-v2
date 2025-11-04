//
//  DefaultApperoTheme.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable

/**
 * Default Appero theme implementation.
 * Uses Material 3 design principles and responds to system light/dark mode.
 *
 * This theme:
 * - Automatically switches between light and dark colors based on system theme
 * - Maintains WCAG 2.2 AA accessibility compliance in both modes
 * - Uses consistent rating colors across light/dark modes for brand recognition
 */
object DefaultApperoTheme : ApperoTheme {
    override val colors: ApperoColors
        @Composable
        get() = if (isSystemInDarkTheme()) {
            ApperoColors(
                background = ApperoDarkColors().background,
                surface = ApperoDarkColors().surface,
                onSurface = ApperoDarkColors().onSurface,
                onSurfaceVariant = ApperoDarkColors().onSurfaceVariant,
                primary = ApperoDarkColors().primary,
                onPrimary = ApperoDarkColors().onPrimary,
                rating1 = ApperoDarkColors().rating1,
                rating2 = ApperoDarkColors().rating2,
                rating3 = ApperoDarkColors().rating3,
                rating4 = ApperoDarkColors().rating4,
                rating5 = ApperoDarkColors().rating5
            )
        } else {
            ApperoColors()
        }

    override val typography: ApperoTypography
        get() = ApperoTypography()

    override val shapes: ApperoShapes
        get() = ApperoShapes()
}

/**
 * Light theme variant (fixed - doesn't respond to system theme).
 * Use this when you want consistent light appearance regardless of system settings.
 */
object LightApperoTheme : ApperoTheme {
    override val colors: ApperoColors
        get() = ApperoColors()

    override val typography: ApperoTypography
        get() = ApperoTypography()

    override val shapes: ApperoShapes
        get() = ApperoShapes()
}

/**
 * Dark theme variant (fixed - doesn't respond to system theme).
 * Use this when you want consistent dark appearance regardless of system settings.
 */
object DarkApperoTheme : ApperoTheme {
    override val colors: ApperoColors
        get() = ApperoColors(
            background = ApperoDarkColors().background,
            surface = ApperoDarkColors().surface,
            onSurface = ApperoDarkColors().onSurface,
            onSurfaceVariant = ApperoDarkColors().onSurfaceVariant,
            primary = ApperoDarkColors().primary,
            onPrimary = ApperoDarkColors().onPrimary,
            rating1 = ApperoDarkColors().rating1,
            rating2 = ApperoDarkColors().rating2,
            rating3 = ApperoDarkColors().rating3,
            rating4 = ApperoDarkColors().rating4,
            rating5 = ApperoDarkColors().rating5
        )

    override val typography: ApperoTypography
        get() = ApperoTypography()

    override val shapes: ApperoShapes
        get() = ApperoShapes()
}
