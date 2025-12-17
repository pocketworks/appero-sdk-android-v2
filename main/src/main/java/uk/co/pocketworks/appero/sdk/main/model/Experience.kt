//
//  Experience.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uk.co.pocketworks.appero.sdk.main.util.ApperoLogger

/**
 * Internal data class representing a logged user experience.
 *
 * @property date Timestamp in milliseconds since epoch of when the experience occurred
 * @property value The experience rating
 * @property detail Optional context string categorizing the experience
 */
@Serializable
internal data class Experience(
    val date: @Serializable(with = InstantSerializer::class) Long,
    val value: ExperienceRating,
    val detail: String? = null,
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
                ApperoLogger.log("Error deserializing Experience: $e")
                null
            }
        }
    }
}
