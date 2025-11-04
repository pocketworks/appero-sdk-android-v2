//
//  ApperoFeedbackUI.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui

import androidx.compose.runtime.Composable
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.theme.DefaultApperoTheme

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
 * @param theme Custom theme configuration (defaults to Material 3 adaptive theme)
 */
@Composable
fun ApperoFeedbackUI(
    apperoInstance: Appero = Appero.instance,
    theme: ApperoTheme = DefaultApperoTheme,
) {
    ApperoFeedbackBottomSheet(
        apperoInstance = apperoInstance,
        theme = theme
    )
}
