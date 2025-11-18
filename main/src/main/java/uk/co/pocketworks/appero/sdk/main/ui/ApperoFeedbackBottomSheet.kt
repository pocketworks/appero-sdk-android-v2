//
//  ApperoFeedbackBottomSheet.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.screens.FeedbackInputScreen
import uk.co.pocketworks.appero.sdk.main.ui.screens.RatingSelectionScreen
import uk.co.pocketworks.appero.sdk.main.ui.screens.ThankYouScreen
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoThemeProvider
import uk.co.pocketworks.appero.sdk.main.ui.theme.localApperoTheme

/**
 * Main feedback bottom sheet modal.
 *
 * Orchestrates the feedback collection flow:
 * 1. Rating Selection Screen - User selects 1-5 rating
 * 2. Feedback Input Screen - User provides optional text feedback
 * 3. Thank You Screen - Confirmation after submission
 *
 * Observes StateFlows from Appero singleton:
 * - shouldShowFeedbackPrompt: Controls visibility
 * - feedbackUIStrings: Title, subtitle, placeholder text
 * - flowType: Determines question (positive/negative)
 *
 * WCAG Compliance:
 * - No focus traps (back button dismisses)
 * - Modal announced to screen readers
 * - Proper state management and transitions
 *
 * @param apperoInstance The Appero SDK instance (defaults to singleton)
 * @param customTheme Custom theme (defaults to Material 3 adaptive theme)
 * @param onDismiss Callback when modal is dismissed
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApperoFeedbackBottomSheet(
    apperoInstance: Appero = Appero.instance,
    customTheme: ApperoTheme? = null,
    onDismiss: () -> Unit = { apperoInstance.dismissApperoPrompt() },
) {
    // Observe StateFlows
    val shouldShow by apperoInstance.shouldShowFeedbackPrompt.collectAsState()
    val uiStrings by apperoInstance.feedbackUIStrings.collectAsState()
    val flowType by apperoInstance.flowType.collectAsState()

    // Internal state
    var currentScreen by remember { mutableStateOf(Screen.Rating) }
    var selectedRating by remember { mutableStateOf<ExperienceRating?>(null) }
    var feedbackText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    // Determine question based on rating
    val questionText = selectedRating?.let { rating ->
        if (rating < ExperienceRating.NEUTRAL) {
            stringResource(R.string.appero_question_negative)
        } else {
            stringResource(R.string.appero_question_positive)
        }
    } ?: ""

    if (shouldShow) {
        ApperoThemeProvider(customTheme) {
            ModalBottomSheet(
                onDismissRequest = {
                    // WCAG: Ensure focus returns properly on dismiss
                    onDismiss()
                    // Reset state for next time
                    currentScreen = Screen.Rating
                    selectedRating = null
                    feedbackText = ""
                    isSubmitting = false
                },
                sheetState = sheetState,
                shape = localApperoTheme.current.shapes.large,
                containerColor = localApperoTheme.current.colors.surface,
            ) {
                Column(
                    modifier = Modifier.padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 32.dp).animateContentSize()
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
                                // Dismiss modal and reset state
                                onDismiss()
                                currentScreen = Screen.Rating
                                selectedRating = null
                                feedbackText = ""
                            },
                        )
                    }

                }
            }
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
