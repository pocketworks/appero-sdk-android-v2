package uk.co.pocketworks.appero.sample.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import uk.co.pocketworks.appero.sample.R
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoColors
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoRatingImages
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoShapes
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoTypography
import uk.co.pocketworks.appero.sdk.main.ui.theme.DefaultApperoRatingImages
import uk.co.pocketworks.appero.sdk.main.ui.theme.DefaultApperoTypography

/**
 * Merriweather serif font family for custom typography
 */
private val MerriweatherFontFamily = FontFamily(
    Font(R.font.merriweather_regular, FontWeight.Normal)
)

/**
 * Theme 1
 */
object CustomTheme1 : ApperoTheme {
    override val colors: ApperoColors = Theme1Colors()
    override val typography: ApperoTypography = DefaultApperoTypography()
    override val shapes: ApperoShapes = ApperoShapes()
    override val ratingImages: ApperoRatingImages = DefaultApperoRatingImages()
}

/**
 * Theme 2 with custom alt_rate images
 * Note: This must be created as a function because AltRatingImages needs a Context
 * to load drawables at initialization time.
 */

fun customTheme2(context: Context): ApperoTheme {
    return object : ApperoTheme {
        override val colors: ApperoColors = Theme2Colors()
        override val typography: ApperoTypography = CustomTypography
        override val shapes: ApperoShapes = ApperoShapes()
        override val ratingImages: ApperoRatingImages = AltRatingImages(context)
    }
}

data class Theme1Colors(
    override val surface: Color = Color(0xFF3A1078),
    override val onSurface: Color = Color(0xFFF8F988),
    override val onSurfaceVariant: Color = Color(0xFF92C7CF),
    override val primary: Color = Color(0xFFE60965),
    override val onPrimary: Color = Color(0xFFFFFFFF),
) : ApperoColors

data class Theme2Colors(
    override val surface: Color = Color(0xFFEFCBC6),
    override val onSurface: Color = Color(0xFF23234D),
    override val onSurfaceVariant: Color = Color(0xFF7254A9),
    override val primary: Color = Color(0xFF3F51B5),
    override val onPrimary: Color = Color(0xFF000000),
) : ApperoColors

object CustomTypography : ApperoTypography {
    override val titleLarge: TextStyle = DefaultApperoTypography().titleLarge.copy(fontFamily = MerriweatherFontFamily)
    override val bodyMedium: TextStyle = DefaultApperoTypography().bodyMedium.copy(fontFamily = MerriweatherFontFamily)
    override val labelLarge: TextStyle = DefaultApperoTypography().labelLarge.copy(fontFamily = MerriweatherFontFamily)
    override val bodySmall: TextStyle = DefaultApperoTypography().bodySmall.copy(fontFamily = MerriweatherFontFamily)
}

/**
 * Custom rating images using the alternative alt_rate drawables.
 * This demonstrates how to provide custom rating icon images from a different module.
 *
 * All drawables are loaded and converted to bitmaps during initialization to avoid
 * cross-module resource resolution issues at composition time.
 *
 * @param context Android context used to load drawable resources
 */
class AltRatingImages(context: Context) : ApperoRatingImages {

    // Load all bitmaps on initialization
    private val bitmapCache: Map<ExperienceRating, BitmapPainter> = mapOf(
        ExperienceRating.STRONG_NEGATIVE to loadBitmap(context, R.drawable.ic_1_star),
        ExperienceRating.NEGATIVE to loadBitmap(context, R.drawable.ic_2_star),
        ExperienceRating.NEUTRAL to loadBitmap(context, R.drawable.ic_3_star),
        ExperienceRating.POSITIVE to loadBitmap(context, R.drawable.ic_4_star),
        ExperienceRating.STRONG_POSITIVE to loadBitmap(context, R.drawable.ic_5_star)
    )

    @Composable
    override fun getPainterForRating(rating: ExperienceRating): Painter {
        // Simply return the pre-loaded bitmap painter for the requested rating
        return bitmapCache[rating] ?: throw IllegalArgumentException("Unknown rating: $rating")
    }

    /**
     * Loads a drawable resource and converts it to a BitmapPainter.
     * This is called during class initialization.
     */
    private fun loadBitmap(context: Context, resId: Int): BitmapPainter {
        val drawable = ResourcesCompat.getDrawable(context.resources, resId, context.theme)
            ?: throw IllegalStateException("Unable to load drawable with resource ID $resId")
        return BitmapPainter(drawable.toBitmap().asImageBitmap())
    }
}