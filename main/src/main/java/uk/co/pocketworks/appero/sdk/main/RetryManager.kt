//
//  RetryManager.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIClient
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIResponse
import uk.co.pocketworks.appero.sdk.main.network.NetworkMonitor
import uk.co.pocketworks.appero.sdk.main.storage.ApperoDataStorage
import uk.co.pocketworks.appero.sdk.main.util.ApperoLogger
import uk.co.pocketworks.appero.sdk.main.util.DateUtils

/**
 * Internal class managing periodic retry of queued experiences and feedback.
 * Processes queues every 3 minutes when network connectivity is available.
 */
@Suppress("LongParameterList")
internal class RetryManager(
    private val storage: ApperoDataStorage,
    private val networkMonitor: NetworkMonitor,
    private val apiKey: String?,
    private val userId: String?,
    private val isDebug: Boolean,
    private val onDataUpdate: (uk.co.pocketworks.appero.sdk.main.model.ApperoData) -> Unit,
    private val scope: CoroutineScope,
) {
    private var retryJob: Job? = null
    private val retryIntervalMs = 180_000L // 3 minutes

    /**
     * Starts the retry mechanism.
     * Will periodically attempt to process queued items.
     */
    fun start() {
        if (retryJob?.isActive == true) {
            return // Already running
        }

        retryJob = scope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(retryIntervalMs)

                val isConnected = networkMonitor.isConnected.first()
                if (!isConnected || networkMonitor.forceOfflineMode) {
                    ApperoLogger.log("No connectivity - skipping retry")
                    continue
                }

                ApperoLogger.log("Attempting to send queued experiences/feedback")
                processUnsentExperiences()
                processUnsentFeedback()
            }
        }
    }

    /**
     * Stops the retry mechanism.
     */
    fun stop() {
        retryJob?.cancel()
        retryJob = null
    }

    /**
     * Processes queued experiences, attempting to send them to the API.
     */
    private suspend fun processUnsentExperiences() {
        if (apiKey == null || userId == null) {
            ApperoLogger.log("Cannot process experiences - API key or user ID not set")
            return
        }

        val currentData = storage.load().getOrNull() ?: return
        val queuedExperiences = currentData.unsentExperiences

        if (queuedExperiences.isEmpty()) {
            return
        }

        ApperoLogger.log("Processing ${queuedExperiences.size} unsent experiences")

        val successfullyProcessed = mutableListOf<uk.co.pocketworks.appero.sdk.main.model.Experience>()

        for ((index, experience) in queuedExperiences.withIndex()) {
            val experienceData = mapOf(
                "client_id" to userId,
                "sent_at" to DateUtils.toIso8601String(experience.date),
                "value" to experience.value.value,
                "context" to (experience.detail ?: ""),
                "source" to "Android",
                "build_version" to BuildConfig.SDK_VERSION
            )

            val result = ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = experienceData,
                method = ApperoAPIClient.HttpMethod.POST,
                authorization = apiKey,
                isDebug = isDebug
            )

            when (result) {
                is ApperoAPIResponse.Success -> {
                    ApperoLogger.log("Experience posted successfully")
                    successfullyProcessed.add(experience)
                }
                is ApperoAPIResponse.Error -> {
                    ApperoLogger.log(
                        "Failed to send queued experience ${index + 1}/${queuedExperiences.size}: ${result.error}"
                    )
                }
            }
        }

        // Remove successfully processed experiences
        if (successfullyProcessed.isNotEmpty()) {
            val updatedData = currentData.copy(
                unsentExperiences = queuedExperiences.filterNot { it in successfullyProcessed }
            )
            storage.save(updatedData)
            onDataUpdate(updatedData)
        }
    }

    /**
     * Processes queued feedback, attempting to send it to the API.
     */
    private suspend fun processUnsentFeedback() {
        if (apiKey == null || userId == null) {
            ApperoLogger.log("Cannot process feedback - API key or user ID not set")
            return
        }

        val currentData = storage.load().getOrNull() ?: return
        val queuedFeedback = currentData.unsentFeedback

        if (queuedFeedback.isEmpty()) {
            return
        }

        ApperoLogger.log("Processing ${queuedFeedback.size} unsent feedback items")

        val successfullyProcessed = mutableListOf<uk.co.pocketworks.appero.sdk.main.model.QueuedFeedback>()

        for ((index, feedback) in queuedFeedback.withIndex()) {
            val feedbackData = mapOf(
                "client_id" to userId,
                "date" to DateUtils.toIso8601String(feedback.date),
                "rating" to feedback.rating.toString(),
                "feedback" to (feedback.feedback ?: ""),
                "source" to "Android",
                "build_version" to BuildConfig.SDK_VERSION
            )

            val result = ApperoAPIClient.sendRequest(
                endpoint = "feedback",
                fields = feedbackData,
                method = ApperoAPIClient.HttpMethod.POST,
                authorization = apiKey,
                isDebug = isDebug
            )

            when (result) {
                is ApperoAPIResponse.Success -> {
                    ApperoLogger.log("Feedback posted successfully")
                    successfullyProcessed.add(feedback)
                }
                is ApperoAPIResponse.Error -> {
                    ApperoLogger.log(
                        "Failed to send queued feedback ${index + 1}/${queuedFeedback.size}: ${result.error}"
                    )
                }
            }
        }

        // Remove successfully processed feedback
        if (successfullyProcessed.isNotEmpty()) {
            val updatedData = currentData.copy(
                unsentFeedback = queuedFeedback.filterNot { it in successfullyProcessed }
            )
            storage.save(updatedData)
            onDataUpdate(updatedData)
        }
    }
}
