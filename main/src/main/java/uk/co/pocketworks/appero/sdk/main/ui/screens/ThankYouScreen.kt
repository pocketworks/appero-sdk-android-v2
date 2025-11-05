//
//  ThankYouScreen.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.ui.theme.ApperoThemeProvider
import uk.co.pocketworks.appero.sdk.main.ui.theme.localApperoTheme

/**
 * Thank you confirmation screen.
 *
 * Displays after feedback is successfully submitted:
 * - Close button (X) in top-right corner
 * - "Thank you!" title
 * - Thank you message explaining the value of feedback
 * - "Done" button to dismiss
 *
 * WCAG Compliance:
 * - Live region announcement for screen readers
 * - Proper heading for title
 * - Centered, focused layout
 * - Button minimum 48dp height
 *
 * @param onDone Callback when "Done" button is tapped
 * @param onClose Callback when close button is tapped
 * @param modifier Optional modifier for customization
 */
@Composable
fun ThankYouScreen(
    onDone: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val theme = localApperoTheme.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .semantics {
                // WCAG: Announce success to screen readers
                liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.End)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.appero_close),
                tint = theme.colors.onSurface
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Thank you title
        Text(
            text = stringResource(R.string.appero_thank_you_title),
            style = theme.typography.titleLarge,
            color = theme.colors.onSurface,
            modifier = Modifier.semantics { heading() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Thank you message
        Text(
            text = stringResource(R.string.appero_thank_you_message),
            style = theme.typography.bodyMedium,
            color = theme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Done button
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp), // WCAG: Minimum touch target height
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.colors.primary,
                contentColor = theme.colors.onPrimary
            ),
            shape = theme.shapes.small
        ) {
            Text(
                text = stringResource(R.string.appero_done),
                style = theme.typography.labelLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ThankYouScreenPreview() {
    ApperoThemeProvider {
        ThankYouScreen(
            onDone = {},
            onClose = {}
        )
    }
}
