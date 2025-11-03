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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIClient
import uk.co.pocketworks.appero.sdk.main.network.NetworkMonitor
import uk.co.pocketworks.appero.sdk.main.storage.ApperoDataStorage
import uk.co.pocketworks.appero.sdk.main.util.ApperoDebug

/**
 * Internal class managing periodic retry of queued experiences and feedback.
 * Processes queues every 3 minutes when network connectivity is available.
 */
internal class RetryManager(
    private val storage: ApperoDataStorage,
    private val networkMonitor: NetworkMonitor,
    private val apiKey: String?,
    private val userId: String?,
    private val isDebug: Boolean,
    private val onDataUpdate: (uk.co.pocketworks.appero.sdk.main.model.ApperoData) -> Unit,
    private val scope: CoroutineScope
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
                    ApperoDebug.log("No connectivity - skipping retry", isDebug)
                    continue
                }
                
                ApperoDebug.log("Attempting to send queued experiences/feedback", isDebug)
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
            ApperoDebug.log("Cannot process experiences - API key or user ID not set", isDebug)
            return
        }
        
        val currentData = storage.load(isDebug).getOrNull() ?: return
        val queuedExperiences = currentData.unsentExperiences
        
        if (queuedExperiences.isEmpty()) {
            return
        }
        
        ApperoDebug.log("Processing ${queuedExperiences.size} unsent experiences", isDebug)
        
        val successfullyProcessed = mutableListOf<uk.co.pocketworks.appero.sdk.main.model.Experience>()
        
        for ((index, experience) in queuedExperiences.withIndex()) {
            val experienceData = mapOf(
                "client_id" to userId,
                "sent_at" to experience.date.toString(),
                "value" to experience.value.value,
                "context" to (experience.context ?: ""),
                "source" to "Android",
                "build_version" to "n/a" // TODO: Get from BuildConfig if available
            )
            
            val result = ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = experienceData,
                method = ApperoAPIClient.HttpMethod.POST,
                authorization = apiKey,
                isDebug = isDebug
            )
            
            when {
                result.isSuccess -> {
                    ApperoDebug.log("Experience posted successfully", isDebug)
                    successfullyProcessed.add(experience)
                }
                else -> {
                    val error = result.exceptionOrNull()
                    ApperoDebug.log(
                        "Failed to send queued experience ${index + 1}/${queuedExperiences.size}: ${error?.message}",
                        isDebug
                    )
                }
            }
        }
        
        // Remove successfully processed experiences
        if (successfullyProcessed.isNotEmpty()) {
            val updatedData = currentData.copy(
                unsentExperiences = queuedExperiences.filterNot { it in successfullyProcessed }
            )
            storage.save(updatedData, isDebug)
            onDataUpdate(updatedData)
        }
    }
    
    /**
     * Processes queued feedback, attempting to send it to the API.
     */
    private suspend fun processUnsentFeedback() {
        if (apiKey == null || userId == null) {
            ApperoDebug.log("Cannot process feedback - API key or user ID not set", isDebug)
            return
        }
        
        val currentData = storage.load(isDebug).getOrNull() ?: return
        val queuedFeedback = currentData.unsentFeedback
        
        if (queuedFeedback.isEmpty()) {
            return
        }
        
        ApperoDebug.log("Processing ${queuedFeedback.size} unsent feedback items", isDebug)
        
        val successfullyProcessed = mutableListOf<uk.co.pocketworks.appero.sdk.main.model.QueuedFeedback>()
        
        for ((index, feedback) in queuedFeedback.withIndex()) {
            val feedbackData = mapOf(
                "client_id" to userId,
                "date" to feedback.date.toString(),
                "rating" to feedback.rating.toString(),
                "feedback" to (feedback.feedback ?: ""),
                "source" to "Android",
                "build_version" to "n/a" // TODO: Get from BuildConfig if available
            )
            
            val result = ApperoAPIClient.sendRequest(
                endpoint = "feedback",
                fields = feedbackData,
                method = ApperoAPIClient.HttpMethod.POST,
                authorization = apiKey,
                isDebug = isDebug
            )
            
            when {
                result.isSuccess -> {
                    ApperoDebug.log("Feedback posted successfully", isDebug)
                    successfullyProcessed.add(feedback)
                }
                else -> {
                    val error = result.exceptionOrNull()
                    ApperoDebug.log(
                        "Failed to send queued feedback ${index + 1}/${queuedFeedback.size}: ${error?.message}",
                        isDebug
                    )
                }
            }
        }
        
        // Remove successfully processed feedback
        if (successfullyProcessed.isNotEmpty()) {
            val updatedData = currentData.copy(
                unsentFeedback = queuedFeedback.filterNot { it in successfullyProcessed }
            )
            storage.save(updatedData, isDebug)
            onDataUpdate(updatedData)
        }
    }
}

