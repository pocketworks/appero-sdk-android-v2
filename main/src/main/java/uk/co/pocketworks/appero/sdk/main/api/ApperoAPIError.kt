//
//  ApperoAPIError.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.api

import uk.co.pocketworks.appero.sdk.main.model.ApperoErrorResponse

/**
 * Sealed class hierarchy for type-safe API error handling.
 */
internal sealed class ApperoAPIError {
    /** Empty response received */
    object NoData : ApperoAPIError()
    
    /** HTTP error status code */
    data class NetworkError(val statusCode: Int) : ApperoAPIError()
    
    /** Server returned error message (401/422 status codes) */
    data class ServerMessage(val response: ApperoErrorResponse?) : ApperoAPIError()
    
    /** No HTTP response received */
    object NoResponse : ApperoAPIError()
    
    /** Unknown/unexpected error */
    data class UnknownError(val throwable: Throwable) : ApperoAPIError()
}

