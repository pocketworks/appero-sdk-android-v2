package uk.co.pocketworks.appero.sample.xml.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import uk.co.pocketworks.appero.sample.xml.R
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
 * Theme 2
 */
object CustomTheme2 : ApperoTheme {
    override val colors: ApperoColors = Theme2Colors()
    override val typography: ApperoTypography = CustomTypography
    override val shapes: ApperoShapes = ApperoShapes()
    override val ratingImages: ApperoRatingImages = DefaultApperoRatingImages()
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
