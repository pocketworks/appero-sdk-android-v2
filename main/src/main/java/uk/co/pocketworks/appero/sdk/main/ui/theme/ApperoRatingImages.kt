//
//  ApperoRatingImages.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating

/**
 * Rating images configuration for Appero SDK.
 *
 * Provides a way to customize the visual representation of rating icons
 * through the theme system. This allows themes to use different icon sets
 * (emoji, stars, numeric, custom graphics, etc.) without modifying components.
 *
 * The function-based API supports lazy composition and any Painter source:
 * - `painterResource(id = R.drawable.rating_1)` - XML vector drawables
 * - `rememberVectorPainter(...)` - Programmatic vector graphics
 * - `rememberAsyncImagePainter(...)` - Network images
 * - `BitmapPainter(...)` - Bitmap resources
 *
 * Example custom implementation:
 * ```kotlin
 * class StarRatingImages : ApperoRatingImages {
 *     @Composable
 *     override fun getPainterForRating(rating: ExperienceRating): Painter {
 *         return when (rating) {
 *             ExperienceRating.STRONG_NEGATIVE -> painterResource(R.drawable.star_1)
 *             ExperienceRating.NEGATIVE -> painterResource(R.drawable.star_2)
 *             ExperienceRating.NEUTRAL -> painterResource(R.drawable.star_3)
 *             ExperienceRating.POSITIVE -> painterResource(R.drawable.star_4)
 *             ExperienceRating.STRONG_POSITIVE -> painterResource(R.drawable.star_5)
 *         }
 *     }
 * }
 * ```
 */
interface ApperoRatingImages {
    /**
     * Returns a Painter for the given experience rating.
     *
     * This function is called during composition to retrieve the appropriate
     * visual representation for each rating level. Implementations should be
     * idempotent (same input produces same output) for recomposition safety.
     *
     * @param rating The experience rating level (1-5)
     * @return A Painter object representing the rating icon
     */
    @Composable
    fun getPainterForRating(rating: ExperienceRating): Painter
}

/**
 * Default rating images using the built-in emoji drawables.
 *
 * Maps ExperienceRating enum values to drawable resources:
 * - STRONG_NEGATIVE → R.drawable.rating_1 (😡 Very dissatisfied)
 * - NEGATIVE → R.drawable.rating_2 (🙁 Dissatisfied)
 * - NEUTRAL → R.drawable.rating_3 (😐 Neutral)
 * - POSITIVE → R.drawable.rating_4 (🙂 Satisfied)
 * - STRONG_POSITIVE → R.drawable.rating_5 (😄 Very satisfied)
 *
 * These drawables are optimized SVG vectors with emoji-style design
 * that support Color.Unspecified tint for original color preservation.
 */
class DefaultApperoRatingImages : ApperoRatingImages {
    @Composable
    override fun getPainterForRating(rating: ExperienceRating): Painter {
        val iconRes = when (rating) {
            ExperienceRating.STRONG_NEGATIVE -> R.drawable.rating_1
            ExperienceRating.NEGATIVE -> R.drawable.rating_2
            ExperienceRating.NEUTRAL -> R.drawable.rating_3
            ExperienceRating.POSITIVE -> R.drawable.rating_4
            ExperienceRating.STRONG_POSITIVE -> R.drawable.rating_5
        }
        // Note: context parameter not used since resources are in the same module
        return painterResource(id = iconRes)
    }
}
