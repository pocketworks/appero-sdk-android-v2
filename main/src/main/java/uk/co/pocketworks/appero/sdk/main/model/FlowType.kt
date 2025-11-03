//
//  FlowType.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.Serializable

/**
 * Determines the type of feedback flow to display in the UI.
 */
@Serializable
enum class FlowType(val apiValue: String) {
    /** Positive experience flow (API value: "normal") */
    POSITIVE("normal"),
    
    /** Neutral experience flow (API value: "neutral") */
    NEUTRAL("neutral"),
    
    /** Negative/frustration experience flow (API value: "frustration") */
    NEGATIVE("frustration");
    
    companion object {
        /**
         * Returns the FlowType for the given API string value.
         */
        fun fromApiValue(value: String): FlowType {
            return when (value) {
                "normal" -> POSITIVE
                "neutral" -> NEUTRAL
                "frustration" -> NEGATIVE
                else -> NEUTRAL // Default fallback
            }
        }
    }
}

