//
//  ApperoData.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.Serializable

/**
 * Internal data class representing the complete persistent state of the Appero SDK.
 * This is stored in SharedPreferences as a JSON string.
 * 
 * @property unsentExperiences Queue of experiences that failed to send due to network issues
 * @property unsentFeedback Queue of feedback that failed to send due to network issues
 * @property feedbackPromptShouldDisplay Whether the feedback prompt should be shown
 * @property feedbackUIStrings Cached UI strings from the backend
 * @property lastPromptDate Last timestamp in milliseconds since epoch when the rating prompt was shown
 * (for rate limiting)
 * @property flowType The type of flow to display (positive/neutral/negative)
 */
@Serializable
internal data class ApperoData(
    val unsentExperiences: List<Experience> = emptyList(),
    val unsentFeedback: List<QueuedFeedback> = emptyList(),
    val feedbackPromptShouldDisplay: Boolean = false,
    val feedbackUIStrings: FeedbackUIStrings = FeedbackUIStrings(
        title = "Thanks for using our app!",
        subtitle = "Please let us know how we're doing",
        prompt = "Share your thoughts here"
    ),
    val lastPromptDate: @Serializable(with = InstantSerializer::class) Long? = null,
    val flowType: FlowType = FlowType.NEUTRAL
)

