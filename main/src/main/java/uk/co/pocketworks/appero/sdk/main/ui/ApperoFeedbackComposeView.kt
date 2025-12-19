package uk.co.pocketworks.appero.sdk.main.ui

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoThemeProvider
import uk.co.pocketworks.appero.sdk.main.ui.theme.localApperoTheme

/**
 * Compose View wrapper for XML layouts.
 *
 * Allows integration of Appero feedback UI into XML-based Android apps
 * that haven't migrated to Jetpack Compose yet.
 *
 * **Important:** This component uses [ApperoFeedbackContent] directly without a modal
 * wrapper, making it suitable for use in DialogFragments or other containers that
 * already provide modal presentation. This avoids nested modal issues.
 *
 * **XML Usage in DialogFragment:**
 * ```xml
 * <uk.co.pocketworks.appero.sdk.main.ui.xml.ApperoFeedbackComposeView
 *     android:id="@+id/apperoFeedbackView"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content" />
 * ```
 *
 * **Kotlin Usage:**
 * ```kotlin
 * class ApperoFeedbackDialogFragment : DialogFragment() {
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *
 *         val composeView = view.findViewById<ApperoFeedbackComposeView>(R.id.apperoFeedbackView)
 *         composeView.theme = CustomTheme // Optional
 *
 *         // Trigger feedback prompt
 *         Appero.instance.triggerShowFeedbackPrompt()
 *     }
 * }
 * ```
 *
 * **Accessibility:**
 * All WCAG 2.2 AA compliance features are automatically inherited
 * from the underlying Compose implementation.
 *
 * @param context Android context
 * @param attrs XML attributes
 * @param defStyleAttr Default style attribute
 */
class ApperoFeedbackComposeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    /**
     * Optional custom theme.
     * Set this programmatically to use a custom Appero theme.
     *
     * Example:
     * ```kotlin
     * findViewById<ApperoFeedbackComposeView>(R.id.apperoFeedbackView).apply {
     *     theme = CustomApperoTheme
     * }
     * ```
     */
    var theme: ApperoTheme? = null

    /**
     * Optional custom Appero instance.
     * Defaults to Appero.instance singleton.
     */
    var apperoInstance: Appero = Appero.Companion.instance

    @Composable
    override fun Content() {
        ApperoThemeProvider(theme) {
            Surface(
                modifier = Modifier.Companion.fillMaxWidth(),
                color = localApperoTheme.current.colors.surface,
            ) {
                ApperoFeedbackContent(
                    apperoInstance = apperoInstance,
                    onDismiss = { apperoInstance.dismissApperoPrompt() },
                    modifier = Modifier.Companion.padding(
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