package uk.co.pocketworks.appero.sdk.main.ui

import androidx.fragment.app.FragmentActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import uk.co.pocketworks.appero.sdk.main.R
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ApperoFeedbackDialogFragmentTest {

    @Test
    fun onDismissRequest_dismissesDialogFragment() {
        val activity = Robolectric.buildActivity(FragmentActivity::class.java)
            .setup()
            .get()

        val fragment = ApperoFeedbackDialogFragment()
        fragment.show(activity.supportFragmentManager, "appero-feedback")
        shadowOf(activity.mainLooper).idle()

        val composeView =
            fragment.requireView().findViewById<ApperoFeedbackComposeView>(R.id.apperoFeedbackView)
        val dismissRequest = composeView.onDismissRequest

        assertNotNull(dismissRequest)
        assertNotNull(fragment.dialog)

        dismissRequest.invoke()
        shadowOf(activity.mainLooper).idle()

        assertFalse(fragment.dialog?.isShowing == true)
    }
}
