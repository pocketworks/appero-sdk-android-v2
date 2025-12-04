//
//  ApperoFeedbackUI.kt
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
 * Top-level composable for Appero feedback UI.
 *
 * Add this composable to your app's composition to enable automatic
 * feedback collection. The modal will appear automatically when the
 * Appero SDK determines it's appropriate based on user experiences.
 *
 * **WCAG 2.2 Level AA Accessibility:**
 * This component is fully accessible with:
 * - TalkBack/screen reader compatibility
 * - Minimum 48dp touch targets (64dp for rating icons)
 * - 4.5:1 text contrast, 3:1 UI element contrast
 * - Supports text scaling up to 200%
 * - Proper focus management and keyboard navigation
 * - Screen reader announcements for state changes
 * - Information conveyed by both color and shape
 *
 * **Example usage:**
 * ```kotlin
 * @Composable
 * fun MyApp() {
 *     MaterialTheme {
 *         MyScreen()
 *
 *         // Add Appero feedback UI
 *         ApperoFeedbackUI()
 *     }
 * }
 * ```
 *
 * **With custom theme:**
 * ```kotlin
 * ApperoFeedbackUI(theme = CustomApperoTheme)
 * ```
 *
 * @param apperoInstance The Appero SDK instance (defaults to singleton)
 * @param customTheme Custom theme configuration (defaults to Material 3 adaptive theme)
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
