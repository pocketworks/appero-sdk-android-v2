//
//  ApperoErrorResponse.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Internal data class representing error responses from the API.
 */
@Serializable
internal data class ApperoErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val details: ErrorDetails? = null,
) {
    /**
     * Returns a formatted error description string.
     */
    fun description(): String {
        val builder = StringBuilder()
        error?.let { builder.append(it).append("\n") }
        message?.let { builder.append("         Server message: $it\n") }
        details?.let {
            it.userId?.firstOrNull()?.let { userId ->
                builder.append("         > $userId\n")
            }
            it.value?.firstOrNull()?.let { value ->
                builder.append("         > $value")
            }
        }
        return builder.toString()
    }
}

/**
 * Internal data class representing error details from the API.
 */
@Serializable
internal data class ErrorDetails(
    @SerialName("user_id")
    val userId: List<String>? = null,
    val value: List<String>? = null,
)
