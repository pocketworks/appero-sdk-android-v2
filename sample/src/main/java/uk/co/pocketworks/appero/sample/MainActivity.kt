//
//  MainActivity.kt
//  Appero SDK Sample
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.co.pocketworks.appero.sample.components.RatingDemoButton
import uk.co.pocketworks.appero.sample.components.ThemeMode
import uk.co.pocketworks.appero.sample.components.ThemeSelector
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.R as SdkR
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.ApperoFeedbackUI
import uk.co.pocketworks.appero.sdk.main.ui.theme.DarkApperoTheme
import uk.co.pocketworks.appero.sdk.main.ui.theme.LightApperoTheme

/**
 * Main activity for the Appero SDK sample app.
 *
 * Demonstrates:
 * - Theme switching (System/Light/Dark)
 * - Manual experience logging with all rating levels
 * - Automatic feedback prompt observation
 * - Feedback UI display
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
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
            .background(Color.White)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Theme Selector
            ThemeSelector(
                selectedTheme = selectedTheme,
                onThemeSelected = { selectedTheme = it },
                label = stringResource(R.string.theme_label)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Rating Buttons
            RatingDemoButton(
                label = stringResource(R.string.rating_very_positive),
                iconRes = SdkR.drawable.rating_5,
                backgroundColor = Color(0xFFD4F4DD),
                onClick = {
                    Appero.instance.log(ExperienceRating.STRONG_POSITIVE)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingDemoButton(
                label = stringResource(R.string.rating_positive),
                iconRes = SdkR.drawable.rating_4,
                backgroundColor = Color(0xFFE8F5E9),
                onClick = {
                    Appero.instance.log(ExperienceRating.POSITIVE)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingDemoButton(
                label = stringResource(R.string.rating_neutral),
                iconRes = SdkR.drawable.rating_3,
                backgroundColor = Color(0xFFFFF3E0),
                onClick = {
                    Appero.instance.log(ExperienceRating.NEUTRAL)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingDemoButton(
                label = stringResource(R.string.rating_negative),
                iconRes = SdkR.drawable.rating_2,
                backgroundColor = Color(0xFFFFEBEE),
                onClick = {
                    Appero.instance.log(ExperienceRating.NEGATIVE)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RatingDemoButton(
                label = stringResource(R.string.rating_very_negative),
                iconRes = SdkR.drawable.rating_1,
                backgroundColor = Color(0xFFFFCDD2),
                onClick = {
                    Appero.instance.log(ExperienceRating.STRONG_NEGATIVE)
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Manual Trigger Button
            RatingDemoButton(
                label = stringResource(R.string.manually_trigger),
                iconRes = android.R.drawable.ic_menu_info_details,
                backgroundColor = Color(0xFFF0F0F0),
                textColor = Color(0xFF007AFF),
                onClick = {
                    // Log a positive rating to trigger feedback prompt
                    Appero.instance.log(ExperienceRating.POSITIVE)
                    Appero.instance.log(ExperienceRating.POSITIVE)
                    Appero.instance.log(ExperienceRating.POSITIVE)
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Appero Feedback UI
        if (shouldShowFeedback) {
            val customTheme = when (selectedTheme) {
                ThemeMode.LIGHT -> LightApperoTheme
                ThemeMode.DARK -> DarkApperoTheme
                ThemeMode.SYSTEM -> null // Use default Material 3 theme
            }

            ApperoFeedbackUI(customTheme = customTheme)
        }
    }
}
