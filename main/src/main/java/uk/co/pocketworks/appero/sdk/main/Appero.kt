//
//  Appero.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2024 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main

import android.content.Context
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID
import uk.co.pocketworks.appero.sdk.main.analytics.IApperoAnalytics
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIError
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIClient
import uk.co.pocketworks.appero.sdk.main.model.ApperoData
import uk.co.pocketworks.appero.sdk.main.model.Experience
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.model.ExperienceResponse
import uk.co.pocketworks.appero.sdk.main.model.FeedbackUIStrings
import uk.co.pocketworks.appero.sdk.main.model.FlowType
import uk.co.pocketworks.appero.sdk.main.model.QueuedFeedback
import uk.co.pocketworks.appero.sdk.main.network.NetworkMonitor
import uk.co.pocketworks.appero.sdk.main.storage.ApperoDataStorage
import uk.co.pocketworks.appero.sdk.main.storage.UserPreferences
import uk.co.pocketworks.appero.sdk.main.util.ApperoDebug

/**
 * Main singleton class for the Appero SDK.
 * 
 * Provides a shared instance that can be accessed from anywhere in your code once initialized.
 * We recommend initializing Appero in your Application class.
 * 
 * Example usage:
 * ```
 * class MyApplication : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         Appero.instance.start(
 *             context = this,
 *             apiKey = "your_api_key",
 *             userId = "optional_user_id"
 *         )
 *     }
 * }
 * 
 * // Log an experience
 * Appero.instance.log(ExperienceRating.STRONG_POSITIVE, "User completed purchase")
 * 
 * // Observe feedback prompt state
 * Appero.instance.shouldShowFeedbackPrompt.collect { shouldShow ->
 *     if (shouldShow) {
 *         // Show feedback UI
 *     }
 * }
 * ```
 */
object Appero {
    /**
     * Shared singleton instance.
     */
    val instance: Appero = this
    
    /**
     * Notification name constant for XML-based apps.
     * Use this with NotificationCenter or similar to observe feedback prompt events.
     */
    const val kApperoFeedbackPromptNotification = "kApperoFeedbackPromptNotification"
    
    // Private configuration
    private var apiKey: String? = null
    
    // Public properties
    /**
     * Optional user identifier (UUID from backend, account number, email address, etc.).
     * If not provided during initialization, a UUID will be generated automatically.
     */
    var userId: String? = null
        private set
    
    /**
     * Set to true to enable debug logging to the console.
     */
    var isDebug: Boolean = false
        set(value) {
            field = value
        }
    
    /**
     * Optional delegate for analytics integration.
     */
    var analyticsDelegate: IApperoAnalytics? = null
    
    // Internal mutable StateFlows
    private val shouldShowFeedbackPromptState = MutableStateFlow(false)
    private val feedbackUIStringsState = MutableStateFlow(
        FeedbackUIStrings(
            title = "Thanks for using our app!",
            subtitle = "Please let us know how we're doing",
            prompt = "Share your thoughts here"
        )
    )
    private val flowTypeState = MutableStateFlow(FlowType.NEUTRAL)
    
    /**
     * Indicates whether the feedback UI should be shown.
     * Use this StateFlow in Compose or observe it to trigger UI presentation.
     */
    val shouldShowFeedbackPrompt: StateFlow<Boolean> = shouldShowFeedbackPromptState.asStateFlow()
    
    /**
     * The UI strings for the feedback interface.
     * These can be customized via the Appero dashboard.
     */
    val feedbackUIStrings: StateFlow<FeedbackUIStrings> = feedbackUIStringsState.asStateFlow()
    
    /**
     * The flow type to display in the feedback UI (positive/neutral/negative).
     */
    val flowType: StateFlow<FlowType> = flowTypeState.asStateFlow()
    
    // Internal components
    private var networkMonitor: NetworkMonitor? = null
    private var storage: ApperoDataStorage? = null
    private var userPreferences: UserPreferences? = null
    private var retryManager: RetryManager? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    // Internal state
    private val apperoDataState = MutableStateFlow<ApperoData?>(null)
    
    /**
     * Initializes the Appero SDK.
     * This should be called early in your app's lifecycle, typically in Application.onCreate().
     * 
     * @param context Application context
     * @param apiKey Your Appero API key
     * @param userId Optional user identifier. If null, a UUID will be generated automatically.
     */
    fun start(context: Context, apiKey: String, userId: String? = null) {
        if (apiKey.isBlank()) {
            ApperoDebug.log("API key cannot be empty", isDebug)
            return
        }
        
        this.apiKey = apiKey
        val applicationContext = context.applicationContext
        
        // Initialize storage
        userPreferences = UserPreferences(applicationContext)
        storage = ApperoDataStorage(applicationContext)
        
        // Set or generate user ID
        this.userId = userId ?: generateOrRestoreUserId(applicationContext)
        
        // Initialize network monitoring
        networkMonitor = NetworkMonitor(applicationContext).also {
            it.forceOfflineMode = false // Can be set externally for testing
        }
        
        // Load existing ApperoData
        val loadedData = storage?.load(isDebug)?.getOrNull() ?: ApperoData()
        apperoDataState.value = loadedData
        
        // Update StateFlows with loaded data
        updateStateFlows(loadedData)
        
        // Initialize retry manager
        val storageInstance = storage
        val networkMonitorInstance = networkMonitor
        if (storageInstance != null && networkMonitorInstance != null) {
            retryManager = RetryManager(
                storage = storageInstance,
                networkMonitor = networkMonitorInstance,
                apiKey = apiKey,
                userId = this.userId,
                isDebug = isDebug,
                onDataUpdate = { data ->
                    apperoDataState.value = data
                    updateStateFlows(data)
                },
                scope = scope
            )
        }
        
        retryManager?.start()
        
        ApperoDebug.log("Appero SDK initialized", isDebug)
    }
    
    /**
     * Generates a unique user ID or restores an existing one from SharedPreferences.
     * 
     * @param context Application context needed to access SharedPreferences
     * @return A unique user ID string
     */
    private fun generateOrRestoreUserId(context: Context): String {
        val prefs = UserPreferences(context)
        val existingId = prefs.getUserId()
        return if (existingId != null) {
            existingId
        } else {
            val newId = UUID.randomUUID().toString()
            prefs.saveUserId(newId)
            newId
        }
    }
    
    /**
     * Logs a user experience.
     * 
     * @param experience The experience rating
     * @param context Optional context string to categorize the experience
     */
    fun log(experience: ExperienceRating, context: String? = null) {
        if (apiKey == null) {
            ApperoDebug.log("Cannot log experience - SDK not initialized", isDebug)
            return
        }
        
        val experienceRecord = Experience(
            date = java.time.Instant.now(),
            value = experience,
            context = context
        )
        
        // Note: We can't pass context here as log() doesn't have it
        // This is fine since storage is initialized during start()
        scope.launch(Dispatchers.IO) {
            postExperience(experienceRecord)
        }
    }
    
    /**
     * Posts user feedback to Appero.
     * 
     * @param rating A value between 1 and 5 inclusive
     * @param feedback Optional feedback text. Maximum length of 500 characters
     * @return Result indicating success or failure
     */
    suspend fun postFeedback(rating: Int, feedback: String?): Result<Boolean> {
        // Validate rating
        if (rating !in 1..5) {
            ApperoDebug.log("Invalid rating: $rating (must be 1-5)", isDebug)
            return Result.failure(IllegalArgumentException("Rating must be between 1 and 5"))
        }
        
        // Validate feedback length
        if (feedback != null && feedback.length > 500) {
            ApperoDebug.log("Feedback exceeds maximum length: ${feedback.length} > 500", isDebug)
            return Result.failure(IllegalArgumentException("Feedback cannot exceed 500 characters"))
        }
        
        val queuedFeedback = QueuedFeedback(
            date = java.time.Instant.now(),
            rating = rating,
            feedback = feedback
        )
        
        val isConnected = networkMonitor?.isConnected?.first() ?: false
        
        if (!isConnected || networkMonitor?.forceOfflineMode == true) {
            ApperoDebug.log("No network connectivity - queuing feedback", isDebug)
            queueFeedback(queuedFeedback, null) // Context not available in suspend function
            return Result.success(true) // Return true since we've queued it
        }
        
        if (apiKey == null) {
            ApperoDebug.log("Cannot send feedback - API key not set", isDebug)
            queueFeedback(queuedFeedback, null) // Context not available in suspend function
            return Result.success(true)
        }
        
        val feedbackData = mapOf(
            "client_id" to (userId ?: ""),
            "date" to queuedFeedback.date.toString(),
            "rating" to queuedFeedback.rating.toString(),
            "feedback" to (queuedFeedback.feedback ?: ""),
            "source" to "Android",
            "build_version" to "n/a"
        )
        
        val apiKeyValue = apiKey ?: run {
            ApperoDebug.log("Cannot send feedback - API key not set", isDebug)
            queueFeedback(queuedFeedback)
            return Result.success(true)
        }
        
        val result = ApperoAPIClient.sendRequest(
            endpoint = "feedback",
            fields = feedbackData,
            method = ApperoAPIClient.HttpMethod.POST,
            authorization = apiKeyValue,
            isDebug = isDebug
        )
        
        return when {
            result.isSuccess -> {
                ApperoDebug.log("Feedback posted successfully", isDebug)
                Result.success(true)
            }
            else -> {
                ApperoDebug.log("Error submitting feedback - queuing for retry", isDebug)
                queueFeedback(queuedFeedback, null) // Context not available in suspend function
                Result.success(true) // Return true since we've queued it
            }
        }
    }
    
    /**
     * Dismisses the feedback prompt and prevents it from reappearing
     * until the server notifies us to show it again.
     * 
     * @param context Application context needed for persistence (optional if SDK initialized)
     */
    fun dismissApperoPrompt(context: Context? = null) {
        updateApperoData({ data ->
            data.copy(feedbackPromptShouldDisplay = false)
        }, context)
    }
    
    /**
     * Resets all local data (queued experiences, user ID, etc.).
     * You might use this when the user logs out or when testing your integration.
     */
    fun reset(context: Context) {
        // Clear user ID
        UserPreferences(context).clearUserId()
        
        // Clear ApperoData
        ApperoDataStorage(context).clear(isDebug)
        
        // Reset instance variables
        userId = null
        apperoDataState.value = ApperoData()
        updateStateFlows(ApperoData())
        
        ApperoDebug.log("Appero SDK reset", isDebug)
    }
    
    // Private helper methods
    
    private suspend fun postExperience(experience: Experience, context: Context? = null) {
        val apiKey = this.apiKey ?: run {
            ApperoDebug.log("Cannot send experience - API key not set", isDebug)
            queueExperience(experience, context)
            return
        }
        
        val userId = this.userId ?: run {
            ApperoDebug.log("Cannot send experience - user ID not set", isDebug)
            queueExperience(experience, context)
            return
        }
        
        val isConnected = networkMonitor?.isConnected?.first() ?: false
        
        if (!isConnected || networkMonitor?.forceOfflineMode == true) {
            ApperoDebug.log("No network connectivity - queuing experience", isDebug)
            queueExperience(experience, context)
            return
        }
        
        val experienceData = mapOf(
            "client_id" to userId,
            "sent_at" to experience.date.toString(),
            "value" to experience.value.value,
            "context" to (experience.context ?: ""),
            "source" to "Android",
            "build_version" to "n/a"
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
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val response = json.decodeFromString<ExperienceResponse>(result.getOrThrow().decodeToString())
                    handleExperienceResponse(response)
                } catch (e: Exception) {
                    ApperoDebug.log("Failed to parse experience response: ${e.message}", isDebug)
                }
            }
            else -> {
                val error = result.exceptionOrNull()
                when (error) {
                    is ApperoAPIError.NetworkError -> {
                        ApperoDebug.log("Network error ${error.statusCode} - queuing experience", isDebug)
                    }
                    is ApperoAPIError.ServerMessage -> {
                        ApperoDebug.log("Server error - queuing experience: ${error.response?.description()}", isDebug)
                    }
                    else -> {
                        ApperoDebug.log("Unknown error sending experience - queuing: ${error?.message}", isDebug)
                    }
                }
                queueExperience(experience, context)
            }
        }
    }
    
    private fun queueExperience(experience: Experience, context: Context? = null) {
        updateApperoData({ data ->
            data.copy(
                unsentExperiences = data.unsentExperiences + experience
            )
        }, context)
        ApperoDebug.log("Queued experience for retry. Total queued: ${(apperoDataState.value?.unsentExperiences?.size ?: 0) + 1}", isDebug)
    }
    
    private fun queueFeedback(feedback: QueuedFeedback, context: Context? = null) {
        updateApperoData({ data ->
            data.copy(
                unsentFeedback = data.unsentFeedback + feedback
            )
        }, context)
        ApperoDebug.log("Queued feedback for retry. Total queued: ${(apperoDataState.value?.unsentFeedback?.size ?: 0) + 1}", isDebug)
    }
    
    private fun handleExperienceResponse(response: ExperienceResponse) {
        val currentData = apperoDataState.value ?: ApperoData()
        
        // Only update feedbackPromptShouldDisplay if it's currently false
        // This prevents subsequent responses from changing the value if we're already showing the UI
        val shouldShow = if (currentData.feedbackPromptShouldDisplay) {
            currentData.feedbackPromptShouldDisplay
        } else {
            response.shouldShowFeedbackUI
        }
        
        val updatedData = currentData.copy(
            feedbackPromptShouldDisplay = shouldShow,
            feedbackUIStrings = response.feedbackUI ?: currentData.feedbackUIStrings,
            flowType = response.getFlowTypeEnum()
        )
        
        updateApperoData { updatedData }
        
        if (shouldShow) {
            // TODO: Post notification for XML-based apps if needed
        }
    }
    
    private fun updateApperoData(update: (ApperoData) -> ApperoData, context: Context? = null) {
        val currentData = apperoDataState.value ?: ApperoData()
        val updatedData = update(currentData)
        apperoDataState.value = updatedData
        
        // Persist to storage if available
        val storageInstance = storage ?: context?.let { ApperoDataStorage(it) }
        storageInstance?.save(updatedData, isDebug)
        
        // Update StateFlows
        updateStateFlows(updatedData)
    }
    
    private fun updateStateFlows(data: ApperoData) {
        shouldShowFeedbackPromptState.value = data.feedbackPromptShouldDisplay
        feedbackUIStringsState.value = data.feedbackUIStrings
        flowTypeState.value = data.flowType
    }
}
