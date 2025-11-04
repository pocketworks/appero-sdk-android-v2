//
//  RatingSelectionScreen.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.components.RatingSelector
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoThemeProvider
import uk.co.pocketworks.appero.sdk.main.ui.theme.LOCAL_APPERO_THEME

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
 * @param onClose Callback when close button is tapped
 * @param modifier Optional modifier for customization
 */
@Composable
fun RatingSelectionScreen(
    title: String,
    subtitle: String,
    selectedRating: ExperienceRating?,
    onRatingSelected: (ExperienceRating) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = LOCAL_APPERO_THEME.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // WCAG: Close button with 48x48dp touch target
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.End)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.appero_close),
                tint = theme.colors.onSurface
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // WCAG: Title as heading
        Text(
            text = title,
            style = theme.typography.titleLarge,
            color = theme.colors.onSurface,
            modifier = Modifier.semantics { heading() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = subtitle,
            style = theme.typography.bodyMedium,
            color = theme.colors.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Rating selector
        RatingSelector(
            selectedRating = selectedRating,
            onRatingSelected = onRatingSelected,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun RatingSelectionScreenPreview() {
    ApperoThemeProvider {
        RatingSelectionScreen(
            title = "We're happy to see that you're using Carbs & Cals 🎉",
            subtitle = "Let us know how we're doing",
            selectedRating = null,
            onRatingSelected = {},
            onClose = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RatingSelectionScreenSelectedPreview() {
    ApperoThemeProvider {
        RatingSelectionScreen(
            title = "We're happy to see that you're using Carbs & Cals 🎉",
            subtitle = "Let us know how we're doing",
            selectedRating = ExperienceRating.NEUTRAL,
            onRatingSelected = {},
            onClose = {}
        )
    }
}
