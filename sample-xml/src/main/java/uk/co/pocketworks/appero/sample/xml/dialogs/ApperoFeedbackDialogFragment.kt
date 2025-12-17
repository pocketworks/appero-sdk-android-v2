//
//  ApperoFeedbackDialogFragment.kt
//  Appero SDK Sample XML
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample.xml.dialogs

import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.ui.graphics.toArgb
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import uk.co.pocketworks.appero.sample.xml.R
import uk.co.pocketworks.appero.sample.xml.ThemeMode
import uk.co.pocketworks.appero.sample.xml.components.CustomTheme1
import uk.co.pocketworks.appero.sample.xml.components.CustomTheme2
import uk.co.pocketworks.appero.sample.xml.components.ThemeHolder
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.ui.theme.DarkApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.theme.LightApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.xml.ApperoFeedbackComposeView

/**
 * BottomSheetDialogFragment that showcases ApperoFeedbackComposeView integration
 * for XML-based Android apps.
 *
 * This demonstrates how apps using traditional XML layouts can integrate
 * the Appero feedback UI through BottomSheetDialogFragment.
 *
 * Key features:
 * - Uses ApperoFeedbackComposeView (XML/View-based integration)
 * - Respects the selected theme from MainActivity
 * - Displays as a bottom sheet (slides up from bottom)
 * - Dismissible via back button, outside touch, and swipe down
 */
class ApperoFeedbackDialogFragment : BottomSheetDialogFragment() {

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
            ThemeMode.SYSTEM -> {
                // Detect system dark mode and use appropriate theme
                val isDarkMode =
                    (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
                if (isDarkMode) DarkApperoTheme else LightApperoTheme
            }
        }
        composeView.theme = apperoTheme

        // Set background with rounded top corners and theme's surface color
        val backgroundColor = apperoTheme.colors.surface.toArgb()
        val cornerRadius = resources.getDimension(uk.co.pocketworks.appero.sdk.main.R.dimen.appero_shape_corner_large)

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

        // Configure dialog window
        dialog?.window?.apply {
            // Set background to transparent to show rounded corners
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Appero.instance.dismissApperoPrompt()
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
