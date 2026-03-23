//
//  ThankYouScreen.kt
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoThemeProvider
import uk.co.pocketworks.appero.sdk.main.ui.theme.localApperoTheme

/**
 * Thank you confirmation screen.
 *
 * Displays after feedback is successfully submitted. Has two variants:
 * 1. Standard: "Thank you!" message with "Done" button
 * 2. Rating prompt: softer thank-you copy mentioning the App Store, single "Done" button
 *    that silently triggers the in-app review flow (shown when rating > NEUTRAL and
 *    onRequestReview is provided)
 *
 * WCAG Compliance:
 * - Live region announcement for screen readers
 * - Proper heading for title
 * - Centered, focused layout
 * - Buttons minimum 48dp height
 *
 * @param modifier Optional modifier for customization
 * @param onDone Callback when "Done" button is tapped
 * @param rating Optional rating that triggered this screen (determines variant)
 * @param onRequestReview Optional callback invoked silently on Done when rating > NEUTRAL
 */
@Composable
fun ThankYouScreen(
    modifier: Modifier = Modifier,
    onDone: () -> Unit,
    rating: ExperienceRating? = null,
    onRequestReview: (() -> Unit)? = null,
) {
    val theme = localApperoTheme.current

    // Determine if we should show the rating prompt
    val shouldShowRatingPrompt = rating != null && rating > ExperienceRating.NEUTRAL

    // Select appropriate strings
    val title = stringResource(R.string.appero_thank_you_title)

    val message = if (shouldShowRatingPrompt) {
        stringResource(R.string.appero_rate_us_message)
    } else {
        stringResource(R.string.appero_thank_you_message)
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Title
        Text(
            text = title,
            style = theme.typography.titleLarge,
            color = theme.colors.onSurface,
            modifier = Modifier
                .semantics { heading() }
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Message
        Text(
            text = message,
            style = theme.typography.bodyMedium,
            color = theme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Single Done button
        Button(
            onClick = {
                if (shouldShowRatingPrompt) onRequestReview?.invoke()
                onDone()
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp), // WCAG: Minimum touch target height
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.colors.primary,
                contentColor = theme.colors.onPrimary
            ),
            shape = theme.shapes.medium
        ) {
            Text(
                text = stringResource(R.string.appero_done),
                style = theme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true, name = "Standard Thank You")
@Composable
private fun ThankYouScreenStandardPreview() {
    ApperoThemeProvider {
        ThankYouScreen(onDone = {})
    }
}

@Preview(showBackground = true, name = "Done with Review (Positive)")
@Composable
private fun ThankYouScreenRateUsPreview() {
    ApperoThemeProvider {
        ThankYouScreen(
            rating = ExperienceRating.STRONG_POSITIVE,
            onRequestReview = {},
            onDone = {}
        )
    }
}

@Preview(showBackground = true, name = "Standard Thank You (Neutral)")
@Composable
private fun ThankYouScreenNeutralPreview() {
    ApperoThemeProvider {
        ThankYouScreen(
            rating = ExperienceRating.NEUTRAL,
            onRequestReview = {},
            onDone = {}
        )
    }
}
