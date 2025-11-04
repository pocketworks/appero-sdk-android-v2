//
//  ApperoTypography.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography scale for Appero SDK.
 * Supports text scaling up to 200% (WCAG 2.2 AA requirement).
 *
 * All font sizes are defined in scalable units (sp) to respect
 * user's system font size preferences.
 */
data class ApperoTypography(
    /**
     * Title text style.
     * Used for main headings (e.g., feedback dialog title).
     * WCAG: Large text (18pt+) requires 3:1 contrast ratio.
     */
    val titleLarge: TextStyle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),

    /**
     * Body text style.
     * Used for subtitles, questions, and general content.
     * WCAG: Normal text requires 4.5:1 contrast ratio.
     */
    val bodyMedium: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    /**
     * Label text style.
     * Used for buttons and interactive elements.
     * WCAG: Button text should be clear and readable.
     */
    val labelLarge: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    /**
     * Small body text style.
     * Used for character counters and helper text.
     */
    val bodySmall: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
)
