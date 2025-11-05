//
//  FeedbackInputScreen.kt
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.components.FeedbackTextField
import uk.co.pocketworks.appero.sdk.main.ui.components.RatingSelector
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoThemeProvider
import uk.co.pocketworks.appero.sdk.main.ui.theme.localApperoTheme

/**
 * Feedback input screen.
 *
 * Displays after user selects a rating:
 * - Close button (X) in top-right corner
 * - Title and subtitle
 * - Selected rating (read-only, others desaturated)
 * - Dynamic question based on rating (positive/negative)
 * - Multi-line text input field with character counter
 * - "Send feedback" button with loading state
 *
 * WCAG Compliance:
 * - Scrollable for text scaling up to 200%
 * - Button minimum 48dp height
 * - Loading state announced to screen readers
 * - Proper heading hierarchy
 *
 * @param title Main heading text
 * @param subtitle Secondary text
 * @param selectedRating The rating user selected
 * @param question Dynamic question based on rating
 * @param feedbackText Current feedback text value
 * @param onFeedbackTextChange Callback when feedback text changes
 * @param onSendFeedback Callback when send button is tapped
 * @param isSubmitting Whether feedback is currently being submitted
 * @param onClose Callback when close button is tapped
 * @param modifier Optional modifier
 */
@Composable
fun FeedbackInputScreen(
    title: String,
    subtitle: String,
    selectedRating: ExperienceRating,
    question: String,
    feedbackText: String,
    onFeedbackTextChange: (String) -> Unit,
    onSendFeedback: () -> Unit,
    isSubmitting: Boolean,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = localApperoTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()) // WCAG: Scrollable for text scaling
            .padding(24.dp)
    ) {
        // Close button
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

        // Title
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

        Spacer(modifier = Modifier.height(24.dp))

        // Show selected rating (read-only)
        RatingSelector(
            selectedRating = selectedRating,
            onRatingSelected = {}, // Read-only, no action
            modifier = Modifier.align(Alignment.CenterHorizontally),
            isReadOnly = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dynamic question
        Text(
            text = question,
            style = theme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = theme.colors.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Feedback text field
        FeedbackTextField(
            value = feedbackText,
            onValueChange = onFeedbackTextChange,
            placeholder = stringResource(R.string.appero_feedback_placeholder)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Resolve string for accessibility announcement
        val submittingText = stringResource(R.string.appero_submitting)

        // WCAG: Button with 48dp minimum height, loading state announced
        Button(
            onClick = onSendFeedback,
            enabled = !isSubmitting,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp) // WCAG: Minimum touch target height
                .semantics {
                    if (isSubmitting) {
                        stateDescription = submittingText
                    }
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.colors.primary,
                contentColor = theme.colors.onPrimary
            ),
            shape = theme.shapes.small
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = theme.colors.onPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = stringResource(R.string.appero_send_feedback),
                style = theme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedbackInputScreenPositivePreview() {
    ApperoThemeProvider {
        FeedbackInputScreen(
            title = "We're happy to see that you're using Carbs & Cals 🎉",
            subtitle = "Let us know how we're doing",
            selectedRating = ExperienceRating.POSITIVE,
            question = "What made your experience positive?",
            feedbackText = "",
            onFeedbackTextChange = {},
            onSendFeedback = {},
            isSubmitting = false,
            onClose = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedbackInputScreenNegativePreview() {
    ApperoThemeProvider {
        FeedbackInputScreen(
            title = "We're happy to see that you're using Carbs & Cals 🎉",
            subtitle = "Let us know how we're doing",
            selectedRating = ExperienceRating.NEGATIVE,
            question = "We're sorry you're not enjoying it. Could you tell us what went wrong?",
            feedbackText = "The search feature is not working properly.",
            onFeedbackTextChange = {},
            onSendFeedback = {},
            isSubmitting = false,
            onClose = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedbackInputScreenLoadingPreview() {
    ApperoThemeProvider {
        FeedbackInputScreen(
            title = "We're happy to see that you're using Carbs & Cals 🎉",
            subtitle = "Let us know how we're doing",
            selectedRating = ExperienceRating.STRONG_POSITIVE,
            question = "What made your experience positive?",
            feedbackText = "Love the barcode scanner feature!",
            onFeedbackTextChange = {},
            onSendFeedback = {},
            isSubmitting = true,
            onClose = {}
        )
    }
}
