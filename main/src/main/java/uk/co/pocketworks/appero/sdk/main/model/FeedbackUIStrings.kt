//
//  FeedbackUIStrings.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.Serializable

/**
 * Contains the text strings to display in the feedback UI.
 * These can be customized via the Appero dashboard.
 * 
 * @property title The title text displayed in the feedback modal
 * @property subtitle The subtitle text displayed below the title
 * @property prompt The placeholder text for the feedback input field
 */
@Serializable
data class FeedbackUIStrings(
    val title: String,
    val subtitle: String,
    val prompt: String
)

