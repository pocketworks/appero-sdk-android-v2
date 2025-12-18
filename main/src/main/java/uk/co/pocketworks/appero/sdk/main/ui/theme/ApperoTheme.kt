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

    /**
     * Rating icon images for the theme.
     * Allows customization of rating button visuals without modifying components.
     *
     * Defaults to emoji-style SVG drawables if not overridden.
     * Users can provide custom implementations using any Painter source.
     *
     * @see ApperoRatingImages
     */
    val ratingImages: ApperoRatingImages
}

/**
 * Light theme variant
 */
object LightApperoTheme : ApperoTheme {
    override val colors: ApperoColors = ApperoLightColors()
    override val typography: ApperoTypography = DefaultApperoTypography()
    override val shapes: ApperoShapes = ApperoShapes()
    override val ratingImages: ApperoRatingImages = DefaultApperoRatingImages()
}

/**
 * Dark theme variant
 */
object DarkApperoTheme : ApperoTheme {
    override val colors: ApperoColors = ApperoDarkColors()
    override val typography: ApperoTypography = DefaultApperoTypography()
    override val shapes: ApperoShapes = ApperoShapes()
    override val ratingImages: ApperoRatingImages = DefaultApperoRatingImages()
}

/**
 * CompositionLocal for accessing the current Appero theme.
 * Use LocalApperoTheme.current to access theme values within composables.
 */
val localApperoTheme = staticCompositionLocalOf<ApperoTheme> {
    throw IllegalStateException("LocalApperoTheme not provided")
}
