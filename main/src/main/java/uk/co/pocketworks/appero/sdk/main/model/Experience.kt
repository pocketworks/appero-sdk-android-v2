//
//  Experience.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

/**
 * Internal data class representing a logged user experience.
 * 
 * @property date Timestamp of when the experience occurred
 * @property value The experience rating
 * @property context Optional context string categorizing the experience
 */
@Serializable
internal data class Experience(
    val date: @Serializable(with = InstantSerializer::class) Instant,
    val value: ExperienceRating,
    val context: String? = null
) {
    companion object {
        /**
         * Custom JSON serializer for Instant using ISO8601 format.
         */
        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }
        
        /**
         * Serializes an Experience to JSON string.
         */
        fun toJson(experience: Experience): String {
            return json.encodeToString(experience)
        }
        
        /**
         * Deserializes an Experience from JSON string.
         */
        fun fromJson(jsonString: String): Experience? {
            return try {
                json.decodeFromString<Experience>(jsonString)
            } catch (e: Exception) {
                null
            }
        }
    }
}


