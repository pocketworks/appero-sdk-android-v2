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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import uk.co.pocketworks.appero.sdk.main.analytics.IApperoAnalytics
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIClient
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIError
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIResponse
import uk.co.pocketworks.appero.sdk.main.model.ApperoData
import uk.co.pocketworks.appero.sdk.main.model.Experience
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.model.ExperienceResponse
import uk.co.pocketworks.appero.sdk.main.model.FeedbackUIStrings
import uk.co.pocketworks.appero.sdk.main.model.FlowType
import uk.co.pocketworks.appero.sdk.main.model.QueuedFeedback
import uk.co.pocketworks.appero.sdk.main.network.NetworkMonitor
import uk.co.pocketworks.appero.sdk.main.storage.ApperoDataStorage
import uk.co.pocketworks.appero.sdk.main.storage.UserPreferencesStorage
import uk.co.pocketworks.appero.sdk.main.util.ApperoLogger
import uk.co.pocketworks.appero.sdk.main.util.DateUtils
import java.util.UUID

/**
 * Main singleton class for the Appero SDK.
 *
 * Provides a shared instance that can be accessed from anywhere in your code once initialized.
 * We recommend initializing Appero in your Application class.
 *
 * The class is lifecycle-aware and will automatically clean up resources when the application
 * is destroyed to prevent memory leaks.
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
class Appero private constructor() : LifecycleEventObserver {

    companion object {
        /**
         * Shared singleton instance.
         * Lazily initialized on first access.
         */
        val instance: Appero by lazy { Appero() }

        const val APPERO_FEEDBACK_MAX_LENGTH = 240
    }

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
    private var debug: Boolean = false

    /**
     * Optional delegate for analytics integration.
     */
    var analyticsDelegate: IApperoAnalytics? = null

    // Internal mutable StateFlows
    private val shouldShowFeedbackPromptState = MutableStateFlow(false)
    private var defaultUiStrings = FeedbackUIStrings.default
    private val feedbackUIStringsState = MutableStateFlow(defaultUiStrings)
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
    private var dataStorage: ApperoDataStorage? = null
    private var userPreferencesStorage: UserPreferencesStorage? = null
    private var retryManager: RetryManager? = null
    private var scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isLifecycleObserverRegistered = false

    // Internal state
    private val apperoDataState = MutableStateFlow<ApperoData?>(null)

    init {
        // Register lifecycle observer when instance is created
        registerLifecycleObserver()
    }

    /**
     * Initializes the Appero SDK.
     * This should be called early in your app's lifecycle, typically in Application.onCreate().
     *
     * @param context Application context
     * @param apiKey Your Appero API key
     * @param userId Optional user identifier. If null, a UUID will be generated automatically.
     * @param debug Set to true to enable debug logging to the console. Defaults to false
     */
    fun start(context: Context, apiKey: String, userId: String? = null, debug: Boolean = false) {
        ApperoLogger.init(debug)

        if (apiKey.isBlank()) {
            ApperoLogger.log("API key cannot be empty")
            return
        }

        // Recreate coroutine scope if it was cancelled during cleanup
        if (!scope.isActive) {
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        }

        this.apiKey = apiKey
        this.debug = debug
        val applicationContext = context.applicationContext

        defaultUiStrings = FeedbackUIStrings(
            title = context.getString(R.string.default_ui_title),
            subtitle = context.getString(R.string.default_ui_subtitle),
            prompt = context.getString(R.string.default_ui_prompt)
        )
        feedbackUIStringsState.value = defaultUiStrings

        // Initialize storage
        userPreferencesStorage = UserPreferencesStorage(applicationContext)
        dataStorage = ApperoDataStorage(applicationContext)

        // Set or generate user ID
        this.userId = userId ?: generateOrRestoreUserId()

        // Initialize network monitoring
        networkMonitor = NetworkMonitor(applicationContext).also {
            it.forceOfflineMode = false // Can be set externally for testing
        }

        // Load existing ApperoData
        val loadedData = dataStorage?.load()?.getOrNull() ?: ApperoData()
        apperoDataState.value = loadedData

        // Update StateFlows with loaded data
        updateStateFlows(loadedData)

        // Initialize retry manager
        val storageInstance = dataStorage
        val networkMonitorInstance = networkMonitor
        if (storageInstance != null && networkMonitorInstance != null) {
            retryManager = RetryManager(
                storage = storageInstance,
                networkMonitor = networkMonitorInstance,
                apiKey = apiKey,
                userId = this.userId,
                isDebug = debug,
                onDataUpdate = { data ->
                    apperoDataState.value = data
                    updateStateFlows(data)
                },
                scope = scope
            )
        }

        retryManager?.start()

        ApperoLogger.log("Appero SDK initialized")
    }

    /**
     * Registers this instance as a lifecycle observer to handle app lifecycle events.
     */
    private fun registerLifecycleObserver() {
        if (!isLifecycleObserverRegistered) {
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
            isLifecycleObserverRegistered = true
        }
    }

    /**
     * Lifecycle event observer callback.
     * Handles cleanup when the application is destroyed.
     */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                cleanup()
            }

            else -> {
                // Handle other lifecycle events if needed
            }
        }
    }

    /**
     * Cleans up resources when the application is destroyed.
     * This prevents memory leaks by releasing references and stopping background operations.
     */
    private fun cleanup() {
        ApperoLogger.log("Cleaning up Appero SDK resources")

        // Stop retry manager
        retryManager?.stop()
        retryManager = null

        // Unregister network monitor
        networkMonitor?.unregister()
        networkMonitor = null

        // Cancel coroutine scope
        scope.cancel()

        // Clear storage references (these don't hold Context, but we'll clear them anyway)
        dataStorage = null
        userPreferencesStorage = null

        // Clear other references
        apiKey = null
        userId = null
        analyticsDelegate = null

        // Unregister lifecycle observer
        if (isLifecycleObserverRegistered) {
            ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
            isLifecycleObserverRegistered = false
        }

        ApperoLogger.log("Appero SDK cleanup complete")
    }

    /**
     * Generates a unique user ID or restores an existing one from SharedPreferences.
     *
     * @return A unique user ID string
     */
    private fun generateOrRestoreUserId(): String {
        val existingId = userPreferencesStorage?.getUserId()
        return if (existingId != null) {
            existingId
        } else {
            val newId = UUID.randomUUID().toString()
            userPreferencesStorage?.saveUserId(newId)
            newId
        }
    }

    /**
     * Logs a user experience.
     *
     * @param rating The experience rating
     * @param detail Optional context string to categorize the experience
     */
    fun log(rating: ExperienceRating, detail: String? = null) {
        if (apiKey == null) {
            ApperoLogger.log("Cannot log experience - SDK not initialized")
            return
        }

        val experienceRecord = Experience(
            date = System.currentTimeMillis(),
            value = rating,
            detail = detail
        )

        scope.launch(Dispatchers.IO) {
            postExperience(experienceRecord)
        }
    }

    /**
     * Posts user feedback to Appero.
     *
     * @param rating The experience rating
     * @param feedback Optional feedback text
     * @return Result indicating success or failure
     */
    suspend fun postFeedback(rating: ExperienceRating, feedback: String?): Result<Boolean> {
        // Validate feedback length
        if (feedback != null && feedback.length > APPERO_FEEDBACK_MAX_LENGTH) {
            ApperoLogger.log("Feedback exceeds maximum length: ${feedback.length} > $APPERO_FEEDBACK_MAX_LENGTH")
            return Result.failure(
                IllegalArgumentException("Feedback cannot exceed $APPERO_FEEDBACK_MAX_LENGTH characters")
            )
        }

        val queuedFeedback = QueuedFeedback(
            date = System.currentTimeMillis(),
            rating = rating.value,
            feedback = feedback
        )

        val feedbackData = mapOf(
            "client_id" to (userId ?: ""),
            "date" to DateUtils.toIso8601String(queuedFeedback.date),
            "rating" to queuedFeedback.rating.toString(),
            "feedback" to (queuedFeedback.feedback ?: ""),
            "source" to "Android",
            "build_version" to BuildConfig.SDK_VERSION
        )

        postToAPI(
            endpoint = "feedback",
            requestData = feedbackData,
            item = queuedFeedback,
            queueAction = ::queueFeedback
        )

        // Always return success since queuing counts as success
        return Result.success(true)
    }

    /**
     * Dismisses the feedback prompt and prevents it from reappearing
     * until the server notifies us to show it again.
     */
    fun dismissApperoPrompt() {
        updateApperoData({ data ->
            data.copy(feedbackPromptShouldDisplay = false)
        })
    }

    /**
     * Used for testing purposes only. It's the back-end which notifies when to show the feedback prompt.
     */
    fun triggerShowFeedbackPrompt() {
        updateApperoData({ data ->
            data.copy(feedbackPromptShouldDisplay = true)
        })
    }

    /**
     * Resets all local data (queued experiences, user ID, etc.).
     * You might use this when the user logs out or when testing your integration.
     */
    fun reset() {
        // Clear user ID
        userPreferencesStorage?.clearUserId()

        // Clear ApperoData
        dataStorage?.clear()

        // Reset instance variables
        userId = null
        apperoDataState.value = ApperoData()
        updateStateFlows(ApperoData())

        ApperoLogger.log("Appero SDK reset")
    }

    // Private helper methods

    private suspend fun postExperience(experience: Experience) {
        val userId = this.userId ?: run {
            ApperoLogger.log("Cannot send experience - user ID not set")
            queueExperience(experience)
            return
        }

        val experienceData = mapOf(
            "client_id" to userId,
            "sent_at" to DateUtils.toIso8601String(experience.date),
            "value" to experience.value.value,
            "context" to (experience.detail ?: ""),
            "source" to "Android",
            "build_version" to BuildConfig.SDK_VERSION
        )

        postToAPI(
            endpoint = "experiences",
            requestData = experienceData,
            item = experience,
            queueAction = ::queueExperience,
            onSuccess = { responseBytes ->
                try {
                    val json = Json { ignoreUnknownKeys = true }
                    val response = json.decodeFromString<ExperienceResponse>(responseBytes.decodeToString())
                    handleExperienceResponse(response)
                } catch (e: Exception) {
                    ApperoLogger.log("Failed to parse experience response: ${e.message}")
                }
            }
        )
    }

    /**
     * Generic helper method for posting items to the Appero API.
     * Handles network validation, API key checking, request execution, and error handling.
     *
     * @param endpoint The API endpoint name (e.g., "experiences", "feedback")
     * @param requestData The request data map to send
     * @param item The item being posted (for queuing on failure)
     * @param queueAction Lambda to queue the item if posting fails
     * @param onSuccess Optional lambda to handle successful response
     */
    private suspend fun <T> postToAPI(
        endpoint: String,
        requestData: Map<String, Any>,
        item: T,
        queueAction: (T) -> Unit,
        onSuccess: suspend (ByteArray) -> Unit = {},
    ) {
        val isConnected = networkMonitor?.isConnected?.first() ?: false

        if (!isConnected || networkMonitor?.forceOfflineMode == true) {
            ApperoLogger.log("No network connectivity - queuing $endpoint")
            queueAction(item)
            return
        }

        val apiKeyValue = apiKey ?: run {
            ApperoLogger.log("Cannot send $endpoint - API key not set")
            queueAction(item)
            return
        }

        val result = ApperoAPIClient.sendRequest(
            endpoint = endpoint,
            fields = requestData,
            method = ApperoAPIClient.HttpMethod.POST,
            authorization = apiKeyValue,
            isDebug = debug
        )

        when (result) {
            is ApperoAPIResponse.Success -> {
                ApperoLogger.log("${endpoint.replaceFirstChar { it.uppercase() }} posted successfully")
                onSuccess(result.data)
            }

            is ApperoAPIResponse.Error -> {
                when (val error = result.error) {
                    is ApperoAPIError.NetworkError -> {
                        ApperoLogger.log("Network error ${error.statusCode} - queuing $endpoint")
                    }

                    is ApperoAPIError.ServerMessage -> {
                        ApperoLogger.log("Server error - queuing $endpoint: ${error.response?.description()}")
                    }

                    else -> {
                        ApperoLogger.log("Unknown error sending $endpoint - queuing: $error")
                    }
                }
                queueAction(item)
            }
        }
    }

    private fun queueExperience(experience: Experience) {
        updateApperoData({ data ->
            data.copy(
                unsentExperiences = data.unsentExperiences + experience
            )
        })
        ApperoLogger.log(
            "Queued experience for retry. Total queued: ${(apperoDataState.value?.unsentExperiences?.size ?: 0) + 1}"
        )
    }

    private fun queueFeedback(feedback: QueuedFeedback) {
        updateApperoData({ data ->
            data.copy(
                unsentFeedback = data.unsentFeedback + feedback
            )
        })
        ApperoLogger.log(
            "Queued feedback for retry. Total queued: ${(apperoDataState.value?.unsentFeedback?.size ?: 0) + 1}"
        )
    }

    private fun handleExperienceResponse(response: ExperienceResponse) {
        val currentData = apperoDataState.value ?: ApperoData()

        // Only update feedbackPromptShouldDisplay if it's currently false
        // This prevents subsequent responses from changing the value if we're already showing the UI
        val shouldShow = if (currentData.feedbackPromptShouldDisplay) {
            true
        } else {
            response.shouldShowFeedbackUI
        }

        val updatedData = currentData.copy(
            feedbackPromptShouldDisplay = shouldShow,
            feedbackUIStrings = response.feedbackUI ?: currentData.feedbackUIStrings,
            flowType = response.getFlowTypeEnum()
        )

        updateApperoData { updatedData }
    }

    private fun updateApperoData(update: (ApperoData) -> ApperoData) {
        val currentData = apperoDataState.value ?: ApperoData()
        val updatedData = update(currentData)
        apperoDataState.value = updatedData

        // Persist to storage
        dataStorage?.save(updatedData)

        // Update StateFlows
        updateStateFlows(updatedData)
    }

    private fun updateStateFlows(data: ApperoData) {
        shouldShowFeedbackPromptState.value = data.feedbackPromptShouldDisplay
        feedbackUIStringsState.value = data.feedbackUIStrings ?: defaultUiStrings
        flowTypeState.value = data.flowType
    }
}
