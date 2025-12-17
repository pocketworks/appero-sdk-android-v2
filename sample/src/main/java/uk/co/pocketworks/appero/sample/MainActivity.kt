//
//  MainActivity.kt
//  Appero SDK Sample
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import uk.co.pocketworks.appero.sample.components.CustomTheme1
import uk.co.pocketworks.appero.sample.components.RatingDemoButton
import uk.co.pocketworks.appero.sample.components.ThemeMode
import uk.co.pocketworks.appero.sample.components.ThemeSelector
import uk.co.pocketworks.appero.sample.components.customTheme2
import uk.co.pocketworks.appero.sample.ui.theme.SampleAppTheme
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.ApperoFeedbackBottomSheet

/**
 * Main activity for the Appero SDK sample app.
 *
 * Demonstrates:
 * - Theme switching (System/Light/Dark)
 * - Manual experience logging with all rating levels
 * - Automatic feedback prompt observation
 * - Feedback UI display
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SampleAppTheme {
                SampleApp()
            }
        }
    }
}

@Composable
fun SampleApp() {
    var selectedTheme by remember { mutableStateOf(ThemeMode.SYSTEM) }
    val shouldShowFeedback by Appero.instance.shouldShowFeedbackPrompt.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Title
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Theme Selector
            ThemeSelector(
                selectedTheme = selectedTheme,
                onThemeSelected = {
                    selectedTheme = it
                },
                label = stringResource(R.string.theme_label)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Rating Buttons
            RatingDemoButton(
                label = stringResource(R.string.rating_very_positive),
                iconRes = R.drawable.ic_thumbs_up_double,
                backgroundColor = Color(0xFFD4F4DD),
                onClick = {
                    Appero.instance.log(
                        ExperienceRating.STRONG_POSITIVE,
                        detail = "Strong positive from compose sample app"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingDemoButton(
                label = stringResource(R.string.rating_positive),
                iconRes = R.drawable.ic_thumb_up_single,
                backgroundColor = Color(0xFFE8F5E9),
                onClick = {
                    Appero.instance.log(ExperienceRating.POSITIVE, detail = "Positive from compose sample app")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingDemoButton(
                label = stringResource(R.string.rating_neutral),
                iconRes = R.drawable.ic_thumbs_up_down,
                backgroundColor = Color(0xFFFFF3E0),
                onClick = {
                    Appero.instance.log(ExperienceRating.NEUTRAL, detail = "Neutral from compose sample app")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingDemoButton(
                label = stringResource(R.string.rating_negative),
                iconRes = R.drawable.ic_thumb_down_single,
                backgroundColor = Color(0xFFFFEBEE),
                onClick = {
                    Appero.instance.log(ExperienceRating.NEGATIVE, detail = "Negative from compose sample app")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingDemoButton(
                label = stringResource(R.string.rating_very_negative),
                iconRes = R.drawable.ic_thumbs_down_double,
                backgroundColor = Color(0xFFFFCDD2),
                onClick = {
                    Appero.instance.log(
                        ExperienceRating.STRONG_NEGATIVE,
                        detail = "Strong negative from compose sample app"
                    )
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.weight(1f))

            // Manual Trigger Button
            RatingDemoButton(
                label = stringResource(R.string.manually_trigger),
                iconRes = null,
                backgroundColor = Color(0xFFF0F0F0),
                onClick = {
                    Appero.instance.triggerShowFeedbackPrompt()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Appero Feedback UI
        if (shouldShowFeedback) {
            val theme = when (selectedTheme) {
                ThemeMode.CUSTOM_1 -> CustomTheme1
                ThemeMode.CUSTOM_2 -> customTheme2(LocalContext.current)
                ThemeMode.SYSTEM -> null // Use default Material 3 theme
            }

            ApperoFeedbackBottomSheet(customTheme = theme)
        }
    }
}
