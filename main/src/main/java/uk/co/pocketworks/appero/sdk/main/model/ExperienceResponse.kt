//
//  ExperienceResponse.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Internal data class representing the response from the experiences API endpoint.
 * 
 * @property shouldShowFeedbackUI Whether the feedback UI should be displayed
 * @property flowType The type of flow to display
 * @property feedbackUI Optional UI strings for the feedback interface
 */
@Serializable
internal data class ExperienceResponse(
    @SerialName("should_show_feedback")
    val shouldShowFeedbackUI: Boolean,
    
    @SerialName("flow_type")
    val flowType: String, // API returns string, we'll convert to FlowType
    
    @SerialName("feedback_ui")
    val feedbackUI: FeedbackUIStrings? = null
) {
    /**
     * Converts the API flow_type string to FlowType enum.
     */
    fun getFlowTypeEnum(): FlowType {
        return FlowType.fromApiValue(flowType)
    }
}

