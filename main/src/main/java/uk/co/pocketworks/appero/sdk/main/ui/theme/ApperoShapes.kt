//
//  ApperoShapes.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Shape definitions for Appero SDK components.
 * Defines corner radii and border shapes for UI elements.
 */
data class ApperoShapes(
    /**
     * Small rounded corners for buttons and interactive elements.
     */
    val small: Shape = RoundedCornerShape(8.dp),

    /**
     * Medium rounded corners for cards and containers.
     */
    val medium: Shape = RoundedCornerShape(12.dp),

    /**
     * Large rounded corners for bottom sheets and modals.
     */
    val large: Shape = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    ),

    /**
     * Fully rounded shape for circular elements (rating buttons).
     */
    val circle: Shape = RoundedCornerShape(50)
)
