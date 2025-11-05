//
//  ApperoAPIClient.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import uk.co.pocketworks.appero.sdk.main.model.ApperoErrorResponse
import uk.co.pocketworks.appero.sdk.main.util.ApperoLogger

/**
 * Internal HTTP client for Appero API requests using Ktor.
 */
internal object ApperoAPIClient {
    private const val BASE_URL = "https://app.appero.co.uk/api/v1"
    private const val TIMEOUT_MS = 10_000

    /**
     * HTTP methods supported by the API.
     */
    enum class HttpMethod {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE,
    }

    /**
     * Creates and configures the Ktor HttpClient instance.
     * @param isDebug Whether debug logging is enabled
     */
    fun createHttpClient(isDebug: Boolean = false): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = false
                    }
                )
            }

            engine {
                connectTimeout = TIMEOUT_MS
                socketTimeout = TIMEOUT_MS
            }

            if (isDebug) {
                install(Logging) {
                    level = LogLevel.INFO
                }
            }
        }
    }

    /**
     * Sends an HTTP request to the Appero API.
     *
     * @param endpoint The API endpoint (e.g., "experiences", "feedback")
     * @param fields The request body fields as a map
     * @param method The HTTP method to use
     * @param authorization The Bearer token for authentication
     * @param isDebug Whether debug logging is enabled
     * @return ApperoAPIResponse containing either success data or error information
     */
    suspend fun sendRequest(
        endpoint: String,
        fields: Map<String, Any>,
        method: HttpMethod,
        authorization: String,
        isDebug: Boolean = false,
    ): ApperoAPIResponse {
        val client = createHttpClient(isDebug)

        return try {
            val url = "$BASE_URL/$endpoint"

            ApperoLogger.log("Sending $method request to $url")

            val response: HttpResponse = when (method) {
                HttpMethod.POST -> {
                    client.post(url) {
                        configureRequest(fields, authorization)
                    }
                }
                HttpMethod.GET -> {
                    client.request(url) {
                        this.method = io.ktor.http.HttpMethod.Get
                        configureRequest(fields, authorization)
                    }
                }
                HttpMethod.PUT -> {
                    client.request(url) {
                        this.method = io.ktor.http.HttpMethod.Put
                        configureRequest(fields, authorization)
                    }
                }
                HttpMethod.PATCH -> {
                    client.request(url) {
                        this.method = io.ktor.http.HttpMethod.Patch
                        configureRequest(fields, authorization)
                    }
                }
                HttpMethod.DELETE -> {
                    client.request(url) {
                        this.method = io.ktor.http.HttpMethod.Delete
                        configureRequest(fields, authorization)
                    }
                }
            }

            handleResponse(response)
        } catch (e: Exception) {
            ApperoLogger.log("Request failed with exception: ${e.message}")
            ApperoAPIResponse.Error(ApperoAPIError.UnknownError(e))
        } finally {
            client.close()
        }
    }

    /**
     * Configures the HTTP request with headers and body.
     */
    private fun HttpRequestBuilder.configureRequest(
        fields: Map<String, Any>,
        authorization: String,
    ) {
        headers {
            append("Authorization", "Bearer $authorization")
            append("Content-Type", "application/json; charset=utf-8")
        }

        contentType(ContentType.Application.Json)

        // Convert fields map to JSON string
        val jsonBody = buildJsonObject(fields).toString()
        setBody(jsonBody)
    }

    /**
     * Builds a JsonObject from a map of fields.
     */
    private fun buildJsonObject(fields: Map<String, Any>): JsonObject {
        return buildJsonObject {
            fields.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                    else -> put(key, value.toString())
                }
            }
        }
    }

    /**
     * Handles the HTTP response and converts it to an ApperoAPIResponse.
     */
    private suspend fun handleResponse(
        response: HttpResponse,
    ): ApperoAPIResponse {
        return when {
            response.status.value in 200..204 -> {
                try {
                    val body = response.body<ByteArray>()
                    ApperoLogger.log("Request successful with status ${response.status}")
                    ApperoAPIResponse.Success(body)
                } catch (e: Exception) {
                    ApperoLogger.log("Failed to read response body: ${e.message}")
                    ApperoAPIResponse.Error(ApperoAPIError.NoData)
                }
            }
            response.status == HttpStatusCode.Unauthorized ||
                response.status == HttpStatusCode.UnprocessableEntity -> {
                try {
                    val errorResponse: ApperoErrorResponse = response.body()
                    ApperoLogger.log("Server error: ${errorResponse.description()}")
                    ApperoAPIResponse.Error(ApperoAPIError.ServerMessage(errorResponse))
                } catch (e: Exception) {
                    ApperoLogger.log("Failed to parse error response: ${e.message}")
                    ApperoAPIResponse.Error(ApperoAPIError.NetworkError(response.status.value))
                }
            }
            else -> {
                ApperoLogger.log("Network error with status ${response.status.value}")
                ApperoAPIResponse.Error(ApperoAPIError.NetworkError(response.status.value))
            }
        }
    }
}
