//
//  RatingSelectionScreen.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.components.RatingSelector
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoThemeProvider
import uk.co.pocketworks.appero.sdk.main.ui.theme.localApperoTheme

/**
 * Initial rating selection screen.
 *
 * Displays:
 * - Close button (X) in top-right corner (48x48dp touch target)
 * - Title from backend
 * - Subtitle from backend
 * - 5-point rating selector
 *
 * WCAG Compliance:
 * - Title marked as heading for screen readers
 * - Proper focus order (close button → title → subtitle → ratings)
 * - All interactive elements meet minimum touch target size
 *
 * @param title Main heading text (from FeedbackUIStrings)
 * @param subtitle Secondary text (from FeedbackUIStrings)
 * @param selectedRating Currently selected rating (null if none)
 * @param onRatingSelected Callback when user selects a rating
 * @param modifier Optional modifier for customization
 */
@Composable
fun RatingSelectionScreen(
    title: String,
    subtitle: String,
    selectedRating: ExperienceRating?,
    onRatingSelected: (ExperienceRating) -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = localApperoTheme.current

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // WCAG: Title as heading
        Text(
            text = title,
            style = theme.typography.titleLarge,
            color = theme.colors.onSurface,
            modifier = Modifier
                .semantics { heading() }
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtitle
        Text(
            text = subtitle,
            style = theme.typography.bodyMedium,
            color = theme.colors.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Rating selector
        RatingSelector(
            selectedRating = selectedRating,
            onRatingSelected = onRatingSelected,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RatingSelectionScreenPreview() {
    ApperoThemeProvider {
        RatingSelectionScreen(
            title = "We're happy to see that you're using our app 🎉",
            subtitle = "Let us know how we're doing",
            selectedRating = null,
            onRatingSelected = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RatingSelectionScreenSelectedPreview() {
    ApperoThemeProvider {
        RatingSelectionScreen(
            title = "We're happy to see that you're using our app 🎉",
            subtitle = "Let us know how we're doing",
            selectedRating = ExperienceRating.NEUTRAL,
            onRatingSelected = {},
        )
    }
}
