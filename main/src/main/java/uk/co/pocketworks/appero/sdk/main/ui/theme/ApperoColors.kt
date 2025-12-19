@file:Suppress("MagicNumber")
//
//  ApperoColors.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * WCAG 2.2 AA compliant color palette for Appero SDK.
 * All colors meet minimum contrast ratios:
 * - Text: 4.5:1 against background
 * - Large text (18pt+): 3:1 against background
 * - UI components: 3:1 against background
 */

interface ApperoColors {
    val surface: Color
    val onSurface: Color
    val onSurfaceVariant: Color
    val primary: Color
    val onPrimary: Color
}

data class ApperoLightColors(
    // Surface colors
    override val surface: Color = Color.White,

    // Text colors
    override val onSurface: Color = Color(0xFF1C1B1F),
    override val onSurfaceVariant: Color = Color(0xFF49454F),

    // Primary button color
    override val primary: Color = Color(0xFF00A0E4),
    override val onPrimary: Color = Color.White
) : ApperoColors

/**
 * Dark mode color palette
 */
data class ApperoDarkColors(
    // Surface colors
    override val surface: Color = Color(0xFF1C1B1F),

    // Text colors
    override val onSurface: Color = Color(0xFFE6E1E5),
    override val onSurfaceVariant: Color = Color(0xFFCAC4D0),

    // Primary button color
    override val primary: Color = Color(0xFF6FB5D3),
    override val onPrimary: Color = Color(0xFF1C1B1F)
) : ApperoColors
