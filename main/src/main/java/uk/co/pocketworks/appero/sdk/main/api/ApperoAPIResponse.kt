//
//  ApperoAPIResponse.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.api

/**
 * Sealed class representing the response from an API request.
 * Provides type-safe handling of success and error cases.
 */
internal sealed class ApperoAPIResponse {
    /**
     * Successful API response containing the response data.
     * @property data The response body as ByteArray
     */
    data class Success(val data: ByteArray) : ApperoAPIResponse() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Success

            return data.contentEquals(other.data)
        }

        override fun hashCode(): Int {
            return data.contentHashCode()
        }
    }

    /**
     * Failed API response containing error information.
     * @property error The specific error that occurred
     */
    data class Error(val error: ApperoAPIError) : ApperoAPIResponse()
}
