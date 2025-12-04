//
//  ApperoFeedbackDialogFragment.kt
//  Appero SDK Sample
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uk.co.pocketworks.appero.sample.R
import uk.co.pocketworks.appero.sample.components.CustomTheme1
import uk.co.pocketworks.appero.sample.components.CustomTheme2
import uk.co.pocketworks.appero.sample.components.ThemeMode
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.ui.xml.ApperoFeedbackComposeView

/**
 * DialogFragment that showcases ApperoFeedbackComposeView integration
 * for legacy XML-based Android apps.
 *
 * This demonstrates how apps that haven't fully migrated to Jetpack Compose
 * can still integrate the Appero feedback UI through traditional Android fragments.
 *
 * Key features:
 * - Uses ApperoFeedbackComposeView (XML/View-based integration)
 * - Respects the selected theme from MainActivity
 * - Standard dialog with margins (not full-screen)
 * - Dismissible via back button and outside touch
 */
class ApperoFeedbackDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use standard dialog style with margins
        setStyle(STYLE_NORMAL, 0)
    }

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

        // Apply theme from ThemeHolder
        val apperoTheme = when (ThemeHolder.currentTheme) {
            ThemeMode.CUSTOM_1 -> CustomTheme1
            ThemeMode.CUSTOM_2 -> CustomTheme2
            ThemeMode.SYSTEM -> null  // Use default Material 3 theme
        }
        composeView.theme = apperoTheme

        // Configure dialog window
        dialog?.window?.apply {
            // Set background to transparent to show rounded corners
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        // Trigger feedback prompt
        Appero.instance.triggerShowFeedbackPrompt()
    }

    companion object {
        /**
         * Factory method to create a new instance of ApperoFeedbackDialogFragment
         */
        fun newInstance(): ApperoFeedbackDialogFragment {
            return ApperoFeedbackDialogFragment()
        }
    }
}
