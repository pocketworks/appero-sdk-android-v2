//
//  RatingSelector.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import uk.co.pocketworks.appero.sdk.main.R
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating

/**
 * WCAG-compliant rating selector component.
 *
 * Displays all 5 rating options in a horizontal row with proper:
 * - Radio button group semantics
 * - Group content description for screen readers
 * - Adequate spacing between touch targets (16dp)
 * - Proper focus order (left to right)
 *
 * @param selectedRating Currently selected rating (null if none selected)
 * @param onRatingSelected Callback when a rating is selected
 * @param modifier Optional modifier for customization
 * @param isReadOnly If true, ratings cannot be changed (used in feedback input screen)
 */
@Composable
fun RatingSelector(
    selectedRating: ExperienceRating?,
    onRatingSelected: (ExperienceRating) -> Unit,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
) {
    val groupDescription = stringResource(R.string.appero_rating_group_label)

    Row(
        modifier = modifier.semantics(mergeDescendants = false) {
            // WCAG: Announce group purpose to screen readers
            contentDescription = groupDescription
        },
        horizontalArrangement = Arrangement.SpaceEvenly, // WCAG: Adequate spacing
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display all 5 rating options
        ExperienceRating.entries.forEach { rating ->
            Box(modifier = Modifier.padding(all = 8.dp).height(56.dp).width(56.dp)) {
                RatingButton(
                    rating = rating,
                    isSelected = selectedRating == rating,
                    onClick = {
                        if (!isReadOnly) {
                            onRatingSelected(rating)
                        }
                    }
                )
            }
        }
    }
}
