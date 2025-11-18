//
//  ThemeSelector.kt
//  Appero SDK Sample
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sample.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Theme selection modes.
 */
enum class ThemeMode {
    SYSTEM,
    CUSTOM_1,
    CUSTOM_2
}

/**
 * Theme selector matching iOS segmented control design.
 *
 * Displays three options in a segmented control style:
 * - System (default)
 * - Light
 * - Dark
 *
 * @param selectedTheme Currently selected theme mode
 * @param onThemeSelected Callback when theme is selected
 * @param label Label text displayed above the selector
 */
@Composable
fun ThemeSelector(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    label: String = "Appero UI theme:",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Label
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Segmented control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFE5E5EA), // iOS light gray background
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ThemeOption(
                label = "System",
                isSelected = selectedTheme == ThemeMode.SYSTEM,
                onClick = { onThemeSelected(ThemeMode.SYSTEM) },
                modifier = Modifier.weight(1f)
            )
            ThemeOption(
                label = "Theme 1",
                isSelected = selectedTheme == ThemeMode.CUSTOM_1,
                onClick = { onThemeSelected(ThemeMode.CUSTOM_1) },
                modifier = Modifier.weight(1f)
            )
            ThemeOption(
                label = "Theme 2",
                isSelected = selectedTheme == ThemeMode.CUSTOM_2,
                onClick = { onThemeSelected(ThemeMode.CUSTOM_2) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Individual theme option button in the segmented control.
 */
@Composable
private fun ThemeOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (isSelected) Color.White else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) Color.Black else Color(0xFF8E8E93)
        )
    }
}
