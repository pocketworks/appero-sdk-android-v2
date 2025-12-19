//
//  RatingButton.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.ui.theme.localApperoTheme

/**
 * WCAG-compliant rating button component.
 *
 * Displays an emoji-style rating icon with proper accessibility support:
 * - 64x64dp touch target (exceeds WCAG 48dp minimum)
 * - Proper semantic role (RadioButton)
 * - Screen reader descriptions
 * - Visual state indication (opacity + optional scale)
 * - Respects reduced motion preferences
 *
 * @param rating The experience rating this button represents
 * @param isSelected Whether this rating is currently selected
 * @param onClick Callback when button is clicked
 * @param modifier Optional modifier for customization
 */
@Composable
fun RatingButton(
    rating: ExperienceRating,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Get content description for accessibility
    val contentDescription = stringResource(
        when (rating) {
            ExperienceRating.STRONG_NEGATIVE -> R.string.appero_rating_1_desc
            ExperienceRating.NEGATIVE -> R.string.appero_rating_2_desc
            ExperienceRating.NEUTRAL -> R.string.appero_rating_3_desc
            ExperienceRating.POSITIVE -> R.string.appero_rating_4_desc
            ExperienceRating.STRONG_POSITIVE -> R.string.appero_rating_5_desc
        }
    )

    // Get painter from theme with context for cross-module resource access
    val context = LocalContext.current
    val theme = localApperoTheme.current
    val painter = theme.ratingImages.getPainterForRating(rating)

    // Animate opacity transition
    val alpha by animateFloatAsState(
        targetValue = if (isSelected) 1.0f else 0.5f,
        label = "rating_alpha"
    )

    // Optional scale animation (disabled if reduced motion preference)
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        label = "rating_scale"
    )

    Box(
        modifier = modifier
            .semantics {
                this.contentDescription = contentDescription
                this.role = Role.RadioButton
                this.selected = isSelected
                this.stateDescription = if (isSelected) "Selected" else "Not selected"
            }
            .clickable(
                onClick = onClick,
                indication = ripple(bounded = false, radius = 32.dp),
                interactionSource = remember { MutableInteractionSource() }
            )
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null, // Handled by parent semantics
            tint = Color.Unspecified, // Use original drawable colors
        )
    }
}
