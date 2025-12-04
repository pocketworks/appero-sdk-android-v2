//
//  ApperoFeedbackContent.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.screens.FeedbackInputScreen
import uk.co.pocketworks.appero.sdk.main.ui.screens.RatingSelectionScreen
import uk.co.pocketworks.appero.sdk.main.ui.screens.ThankYouScreen

/**
 * Core feedback flow orchestration without modal presentation wrapper.
 *
 * Orchestrates the 3-screen feedback flow:
 * 1. Rating Selection - User selects 1-5 rating
 * 2. Feedback Input - User provides optional text feedback
 * 3. Thank You - Confirmation after submission
 *
 * This composable contains all flow logic and state management but does NOT
 * include modal presentation (ModalBottomSheet, Dialog, etc.). Wrappers like
 * [ApperoFeedbackBottomSheet] or [ApperoFeedbackComposeView] handle presentation.
 *
 * **Usage in ModalBottomSheet:**
 * ```kotlin
 * ModalBottomSheet(...) {
 *     ApperoFeedbackContent(
 *         apperoInstance = Appero.instance,
 *         onDismiss = { Appero.instance.dismissApperoPrompt() }
 *     )
 * }
 * ```
 *
 * **Usage in Dialog/DialogFragment:**
 * ```kotlin
 * Dialog(onDismissRequest = { ... }) {
 *     Surface {
 *         ApperoFeedbackContent(
 *             apperoInstance = Appero.instance,
 *             onDismiss = { dismiss() }
 *         )
 *     }
 * }
 * ```
 *
 * **WCAG Compliance:**
 * - All child screens implement WCAG 2.2 AA standards
 * - Proper semantic structure for screen readers
 * - Content size adapts to text scaling
 *
 * @param apperoInstance The Appero SDK instance (defaults to singleton)
 * @param onDismiss Callback when flow should be dismissed (e.g., from ThankYou screen)
 * @param modifier Optional modifier for customization
 */
@Composable
fun ApperoFeedbackContent(
    apperoInstance: Appero = Appero.instance,
    onDismiss: () -> Unit = { apperoInstance.dismissApperoPrompt() },
    modifier: Modifier = Modifier,
) {
    // Observe StateFlows from Appero instance
    val uiStrings by apperoInstance.feedbackUIStrings.collectAsState()
    val flowType by apperoInstance.flowType.collectAsState()

    // Internal navigation state
    var currentScreen by remember { mutableStateOf(Screen.Rating) }
    var selectedRating by remember { mutableStateOf<ExperienceRating?>(null) }
    var feedbackText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Determine question based on rating
    val questionText = selectedRating?.let { rating ->
        if (rating < ExperienceRating.NEUTRAL) {
            stringResource(R.string.appero_question_negative)
        } else {
            stringResource(R.string.appero_question_positive)
        }
    } ?: ""

    Column(
        modifier = modifier.animateContentSize()
    ) {
        when (currentScreen) {
            Screen.Rating -> RatingSelectionScreen(
                title = uiStrings.title,
                subtitle = uiStrings.subtitle,
                selectedRating = selectedRating,
                onRatingSelected = { rating ->
                    selectedRating = rating
                    apperoInstance.analyticsDelegate?.logRatingSelected(rating.value)
                    // Navigate to feedback input
                    currentScreen = Screen.FeedbackInput
                }
            )

            Screen.FeedbackInput -> FeedbackInputScreen(
                title = uiStrings.title,
                subtitle = uiStrings.subtitle,
                selectedRating = selectedRating,
                question = questionText,
                feedbackText = feedbackText,
                onFeedbackTextChange = { feedbackText = it },
                isSubmitting = isSubmitting,
                onRatingSelected = { rating ->
                    selectedRating = rating
                    apperoInstance.analyticsDelegate?.logRatingSelected(rating.value)
                },
                onSendFeedback = {
                    isSubmitting = true
                    scope.launch {
                        // Submit feedback to Appero
                        selectedRating?.let { rating ->
                            apperoInstance.postFeedback(
                                rating = rating,
                                feedback = feedbackText.ifBlank { null }
                            )

                            // Call analytics delegate
                            apperoInstance.analyticsDelegate?.logApperoFeedback(
                                rating.value,
                                feedbackText
                            )

                            isSubmitting = false
                            // Navigate to thank you screen
                            currentScreen = Screen.ThankYou
                        }
                    }
                },
            )

            Screen.ThankYou -> ThankYouScreen(
                onDone = {
                    // Dismiss and reset state
                    onDismiss()
                    currentScreen = Screen.Rating
                    selectedRating = null
                    feedbackText = ""
                },
            )
        }
    }
}

/**
 * Internal enum for screen navigation.
 */
private enum class Screen {
    Rating,
    FeedbackInput,
    ThankYou,
}
