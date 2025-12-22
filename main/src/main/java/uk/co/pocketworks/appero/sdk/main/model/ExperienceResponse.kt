//
//  ExperienceResponse.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import uk.co.pocketworks.appero.sdk.main.util.ApperoLogger

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
    val flowType: String,

    @SerialName("feedback_ui")
    val feedbackUI: FeedbackUIStrings? = null,
) {
    /**
     * Converts the API flow_type string to FlowType enum.
     */
    fun getFlowTypeEnum(): FlowType {
        return FlowType.fromApiValue(flowType)
    }

    companion object {
        fun fromBytes(bytes: ByteArray): ExperienceResponse? {
            return try {
                val json = Json { ignoreUnknownKeys = true }
                json.decodeFromString<ExperienceResponse>(bytes.decodeToString())
            } catch (e: Exception) {
                ApperoLogger.log("Failed to parse experience response: ${e.message}")
                null
            }
        }
    }
}
