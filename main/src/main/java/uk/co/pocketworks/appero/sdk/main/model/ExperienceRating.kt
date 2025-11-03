//
//  ExperienceRating.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.Serializable

/**
 * Represents user experience ratings on a 5-point scale.
 * Higher values indicate more positive experiences.
 */
@Serializable
enum class ExperienceRating(val value: Int) {
    /** Strongly positive user experience (rating: 5) */
    STRONG_POSITIVE(5),
    
    /** Positive user experience (rating: 4) */
    POSITIVE(4),
    
    /** Neutral user experience (rating: 3) */
    NEUTRAL(3),
    
    /** Negative user experience (rating: 2) */
    NEGATIVE(2),
    
    /** Strongly negative user experience (rating: 1) */
    STRONG_NEGATIVE(1);
}

