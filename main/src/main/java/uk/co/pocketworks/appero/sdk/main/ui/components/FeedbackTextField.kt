//
//  FeedbackTextField.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.Appero
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.ui.theme.localApperoTheme

/**
 * WCAG-compliant feedback text input field.
 *
 * Features:
 * - Multi-line text input (4-6 lines visible)
 * - Character counter with accessibility announcements
 * - Maximum character limit (240 by default)
 * - Minimum 120dp height for adequate touch area
 * - Proper keyboard configuration
 * - State description for screen readers
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param placeholder Placeholder text shown when empty
 * @param maxLength Maximum number of characters allowed
 * @param modifier Optional modifier for customization
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FeedbackTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxLength: Int = Appero.APPERO_FEEDBACK_MAX_LENGTH,
) {
    val theme = localApperoTheme.current
    val characterCount = value.length

    // Create character counter text for accessibility and display
    val characterCountText = stringResource(
        R.string.appero_character_count,
        characterCount,
        maxLength
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                // Enforce maximum length
                if (newValue.length <= maxLength) {
                    onValueChange(newValue)
                }
            },
            placeholder = {
                Text(
                    text = placeholder,
                    color = theme.colors.onSurfaceVariant,
                    style = theme.typography.bodyMedium
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp) // WCAG: Adequate touch/interaction area
                .semantics {
                    // WCAG: Announce character limit to screen readers
                    stateDescription = characterCountText
                },
            minLines = 4,
            maxLines = 6,
            textStyle = theme.typography.bodyMedium.copy(
                color = theme.colors.onSurface
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = theme.colors.primary,
                unfocusedBorderColor = theme.colors.onSurfaceVariant,
                focusedTextColor = theme.colors.onSurface,
                unfocusedTextColor = theme.colors.onSurface,
                cursorColor = theme.colors.primary
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done
            ),
            shape = theme.shapes.medium
        )

        // WCAG: Visual character counter
        Text(
            text = characterCountText,
            style = theme.typography.bodySmall,
            color = theme.colors.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
                .semantics {
                    // WCAG: Hide from screen readers (already in TextField semantics)
                    invisibleToUser()
                }
        )
    }
}
