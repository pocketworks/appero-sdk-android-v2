//
//  ApperoFeedbackBottomSheet.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoThemeProvider
import uk.co.pocketworks.appero.sdk.main.ui.theme.localApperoTheme

/**
 * Main feedback bottom sheet modal wrapper.
 *
 * Wraps [ApperoFeedbackContent] in a Material 3 [ModalBottomSheet] for Compose-based apps.
 * For XML-based apps or DialogFragment integration, use [ApperoFeedbackComposeView] instead,
 * which uses the content directly without the modal wrapper to avoid nested modals.
 *
 * **Usage:**
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     MyContent()
 *
 *     // Feedback UI appears automatically when triggered
 *     ApperoFeedbackBottomSheet()
 * }
 * ```
 *
 * **Observes StateFlows from Appero singleton:**
 * - shouldShowFeedbackPrompt: Controls visibility
 *
 * **WCAG Compliance:**
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
    // Observe visibility StateFlow
    val shouldShow by apperoInstance.shouldShowFeedbackPrompt.collectAsState()

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (shouldShow) {
        ApperoThemeProvider(customTheme) {
            ModalBottomSheet(
                onDismissRequest = {
                    // WCAG: Ensure focus returns properly on dismiss
                    onDismiss()
                },
                sheetState = sheetState,
                shape = localApperoTheme.current.shapes.large,
                containerColor = localApperoTheme.current.colors.surface,
            ) {
                ApperoFeedbackContent(
                    apperoInstance = apperoInstance,
                    onDismiss = {
                        // Animate sheet hiding, then dismiss
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier.padding(
                        start = 24.dp,
                        top = 12.dp,
                        end = 24.dp,
                        bottom = 32.dp
                    )
                )
            }
        }
    }
}
