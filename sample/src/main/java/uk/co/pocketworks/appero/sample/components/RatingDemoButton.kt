//
//  RatingDemoButton.kt
//  Appero SDK Sample
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Rating button matching iOS sample design.
 *
 * Displays a button with:
 * - Icon (emoji-style rating icon)
 * - Rating label text
 * - Colored background
 * - Full width layout
 *
 * @param label The rating label text (e.g., "Very Positive")
 * @param iconRes The drawable resource ID for the rating icon
 * @param backgroundColor The background color of the button
 * @param onClick Callback when button is clicked
 */
@Composable
fun RatingDemoButton(
    label: String,
    iconRes: Int?,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color(0xFF007AFF)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = label,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF007AFF)
            )
        }
    }
}
