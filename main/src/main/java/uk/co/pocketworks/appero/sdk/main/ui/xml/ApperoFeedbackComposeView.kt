//
//  ApperoFeedbackComposeView.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.xml

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.ui.ApperoFeedbackUI
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoTheme

/**
 * Compose View wrapper for XML layouts.
 *
 * Allows integration of Appero feedback UI into XML-based Android apps
 * that haven't migrated to Jetpack Compose yet.
 *
 * **XML Usage:**
 * ```xml
 * <uk.co.pocketworks.appero.sdk.main.ui.xml.ApperoFeedbackComposeView
 *     android:id="@+id/apperoFeedbackView"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content" />
 * ```
 *
 * **Kotlin Usage:**
 * ```kotlin
 * class MainActivity : AppCompatActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         setContentView(R.layout.activity_main)
 *
 *         // Appero feedback UI is automatically displayed via the XML view
 *         // No additional code needed!
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
    var apperoInstance: Appero = Appero.instance

    @Composable
    override fun Content() {
        ApperoFeedbackUI(
            apperoInstance = apperoInstance,
            customTheme = theme
        )
    }
}
