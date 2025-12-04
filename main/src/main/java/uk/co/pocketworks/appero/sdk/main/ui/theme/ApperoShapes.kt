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
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.R

/**
 * Shape definitions for Appero SDK components.
 * Defines corner radii and border shapes for UI elements.
 *
 * Dimension values are defined in res/values/dimens.xml and can be
 * reused in XML layouts across different modules.
 */
data class ApperoShapes(
    /**
     * Small rounded corners for buttons and interactive elements.
     * References: @dimen/appero_shape_corner_small (8dp)
     */
    val small: Shape = RoundedCornerShape(8.dp),

    /**
     * Medium rounded corners for cards and containers.
     * References: @dimen/appero_shape_corner_medium (12dp)
     */
    val medium: Shape = RoundedCornerShape(12.dp),

    /**
     * Large rounded corners for bottom sheets and modals.
     * References: @dimen/appero_shape_corner_large (28dp)
     */
    val large: Shape = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    ),

    /**
     * Fully rounded shape for circular elements (rating buttons).
     * References: @integer/appero_shape_corner_circle_percent (50%)
     */
    val circle: Shape = RoundedCornerShape(50),
) {
    companion object {
        /**
         * Creates ApperoShapes using dimension resources.
         * Use this in Compose to ensure values match the resource definitions.
         */
        @Composable
        operator fun invoke(): ApperoShapes {
            val context = LocalContext.current
            val smallRadius = dimensionResource(R.dimen.appero_shape_corner_small)
            val mediumRadius = dimensionResource(R.dimen.appero_shape_corner_medium)
            val largeRadius = dimensionResource(R.dimen.appero_shape_corner_large)
            val circlePercent = context.resources.getInteger(R.integer.appero_shape_corner_circle_percent)

            return ApperoShapes(
                small = RoundedCornerShape(smallRadius),
                medium = RoundedCornerShape(mediumRadius),
                large = RoundedCornerShape(
                    topStart = largeRadius,
                    topEnd = largeRadius,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                ),
                circle = RoundedCornerShape(circlePercent)
            )
        }
    }
}
