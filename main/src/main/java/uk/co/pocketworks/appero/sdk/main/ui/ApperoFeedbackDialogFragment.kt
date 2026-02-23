//
//  ApperoFeedbackDialogFragment.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui

import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.graphics.toArgb
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.theme.DarkApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.theme.LightApperoTheme

/**
 * A [BottomSheetDialogFragment] that displays the Appero feedback UI for XML-based Android apps.
 *
 * This fragment provides a ready-to-use bottom sheet dialog containing the complete Appero
 * feedback flow including rating selection, text feedback input, and thank you screens.
 * It's designed for apps using traditional XML layouts that haven't fully migrated to Jetpack Compose.
 *
 * ## Features
 * - Bottom sheet presentation with drag handle for dismissal affordance
 * - Rounded top corners matching Material Design guidelines
 * - Automatic theme application (system light/dark mode or custom themes)
 * - Proper lifecycle handling with automatic cleanup
 * - Full WCAG 2.2 AA accessibility compliance
 *
 * ## Basic Usage
 * ```kotlin
 * // Show feedback dialog with system theme
 * val dialog = ApperoFeedbackDialogFragment()
 * dialog.show(supportFragmentManager, "appero_feedback")
 * ```
 *
 * ## Custom Theme Usage
 * ```kotlin
 * // Show feedback dialog with custom brand theme
 * val dialog = ApperoFeedbackDialogFragment().apply {
 *     customTheme = MyBrandTheme
 * }
 * dialog.show(supportFragmentManager, "appero_feedback")
 * ```
 *
 * ## Integration Pattern
 * The SDK can automatically show this dialog when appropriate by observing the feedback prompt state:
 * ```kotlin
 * lifecycleScope.launch {
 *     Appero.instance.shouldShowPrompt.collect { shouldShow ->
 *         if (shouldShow) {
 *             val dialog = ApperoFeedbackDialogFragment()
 *             dialog.show(supportFragmentManager, "appero_feedback")
 *         }
 *     }
 * }
 * ```
 *
 * ## Dismissal Behavior
 * The dialog can be dismissed by:
 * - Swiping down on the drag handle
 * - Tapping outside the dialog
 * - Pressing the back button
 * - Completing the feedback flow
 *
 * When dismissed, the fragment automatically calls [Appero.dismissApperoPrompt] to update the SDK state.
 *
 * @property customTheme Optional custom theme for the feedback UI. If null, uses the system theme
 * (light or dark based on device settings). See [ApperoTheme], [LightApperoTheme], [DarkApperoTheme].
 *
 * @see ApperoFeedbackComposeView for direct XML layout integration
 * @see ApperoFeedbackBottomSheet for Compose-native apps
 */
class ApperoFeedbackDialogFragment : BottomSheetDialogFragment() {

    /**
     * Optional custom theme for the feedback UI.
     * If null, the dialog will use the system theme (light or dark based on device settings).
     *
     * Set this property before showing the dialog:
     * ```kotlin
     * val dialog = ApperoFeedbackDialogFragment().apply {
     *     customTheme = MyBrandTheme
     * }
     * dialog.show(supportFragmentManager, "appero_feedback")
     * ```
     */
    var customTheme: ApperoTheme? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate XML layout containing ApperoFeedbackComposeView
        return inflater.inflate(R.layout.fragment_appero_feedback_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get reference to ApperoFeedbackComposeView
        val composeView = view.findViewById<ApperoFeedbackComposeView>(R.id.apperoFeedbackView)

        // Apply theme: use custom theme if provided, otherwise detect system theme
        val apperoTheme = customTheme ?: run {
            // Detect system dark mode and use appropriate built-in theme
            val isDarkMode =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            if (isDarkMode) DarkApperoTheme else LightApperoTheme
        }
        composeView.theme = apperoTheme

        // Set background with rounded top corners using theme's surface color
        val backgroundColor = apperoTheme.colors.surface.toArgb()
        val cornerRadius = resources.getDimension(R.dimen.appero_shape_corner_large)

        val background = GradientDrawable().apply {
            setColor(backgroundColor)
            cornerRadii = floatArrayOf(
                cornerRadius, cornerRadius,  // top left
                cornerRadius, cornerRadius,  // top right
                0f, 0f,                      // bottom right
                0f, 0f                       // bottom left
            )
        }
        view.findViewById<LinearLayout>(R.id.apperoFeedbackContainer).background = background

        // Configure dialog window to show rounded corners with blur and dim effect
        dialog?.window?.apply {
            // Set background to transparent so rounded corners are visible
            setBackgroundDrawableResource(android.R.color.transparent)

            // Enable dim background (70% opacity)
            setDimAmount(0.7f)

            // Add blur effect on Android 12+ (API 31+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setBackgroundBlurRadius(80)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Notify SDK that the prompt has been dismissed
        Appero.instance.dismissApperoPrompt()
    }

    companion object {
        /**
         * Factory method to create a new instance of [ApperoFeedbackDialogFragment].
         *
         * @return A new instance of ApperoFeedbackDialogFragment with system theme
         */
        fun newInstance(): ApperoFeedbackDialogFragment {
            return ApperoFeedbackDialogFragment()
        }
    }
}
