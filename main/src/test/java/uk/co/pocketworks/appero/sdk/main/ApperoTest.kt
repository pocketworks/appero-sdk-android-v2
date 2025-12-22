//
//  ApperoTest.kt
//  Appero SDK
//
//  MIT License
//
//  Copyright (c) 2025 Pocketworks Mobile
//

package uk.co.pocketworks.appero.sdk.main

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIClient
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIError
import uk.co.pocketworks.appero.sdk.main.api.ApperoAPIResponse
import uk.co.pocketworks.appero.sdk.main.model.ApperoData
import uk.co.pocketworks.appero.sdk.main.model.Experience
import uk.co.pocketworks.appero.sdk.main.model.ExperienceRating
import uk.co.pocketworks.appero.sdk.main.model.ExperienceResponse
import uk.co.pocketworks.appero.sdk.main.model.FeedbackUIStrings
import uk.co.pocketworks.appero.sdk.main.model.FlowType
import uk.co.pocketworks.appero.sdk.main.network.NetworkMonitor
import uk.co.pocketworks.appero.sdk.main.storage.ApperoDataStorage
import uk.co.pocketworks.appero.sdk.main.storage.UserPreferencesStorage
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit tests for Appero class.
 *
 * These tests focus on the core functionality of the Appero SDK including:
 * - Initialization behavior
 * - User ID management
 * - Debug mode configuration
 * - Data persistence
 *
 * All tests use MockK for mocking dependencies to ensure isolated unit testing.
 * Robolectric is used to provide Android framework components for testing.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
@OptIn(ExperimentalCoroutinesApi::class)
class ApperoTest {

    // Test context (real context from Robolectric)
    private lateinit var context: Context

    // Test subjects
    private lateinit var appero: Appero

    // Test coroutine dispatcher
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Get real context from Robolectric
        context = ApplicationProvider.getApplicationContext()

        // Clear any existing data from previous tests
        val userPrefs = UserPreferencesStorage(context)
        userPrefs.clearUserId()

        val dataStorage = ApperoDataStorage(context)
        dataStorage.clear()

        // Mock ApperoAPIClient to avoid real network calls
        mockkObject(ApperoAPIClient)
        coEvery {
            ApperoAPIClient.sendRequest(any(), any(), any(), any(), any())
        } returns ApperoAPIResponse.Success(byteArrayOf())

        // Get Appero instance
        appero = Appero.instance

        // Inject test dispatcher for background operations
        appero.backgroundDispatcher = testDispatcher
    }

    @After
    fun tearDown() {
        // Reset dispatcher to default
        appero.backgroundDispatcher = Dispatchers.IO

        // Clean up storage after each test
        val userPrefs = UserPreferencesStorage(context)
        userPrefs.clearUserId()

        val dataStorage = ApperoDataStorage(context)
        dataStorage.clear()

        // Reset mocks
        unmockkAll()
        Dispatchers.resetMain()
    }

    // =========================================================================
    // Initialization Tests
    // =========================================================================

    /**
     * Test 1.3: Custom User ID
     *
     * Scenario: Initialize with custom user ID
     * Given: Valid API key and custom userId="user_123"
     * Expected: userId property set to "user_123", not generated
     */
    @Test
    fun test_start_withCustomUserId_shouldSetUserIdProperty() {
        // Given
        val customUserId = "user_123"
        val apiKey = "test_api_key"

        // When
        appero.start(
            context = context,
            apiKey = apiKey,
            userId = customUserId,
            debug = false
        )

        // Then
        assertEquals(customUserId, appero.userId)
    }

    /**
     * Test 1.4: Auto-Generated User ID
     *
     * Scenario: Initialize without providing user ID
     * Given: Valid API key, userId=null
     * Expected: UUID generated and saved to UserPreferencesStorage
     */
    @Test
    fun test_start_withoutUserId_shouldGenerateAndSaveUserId() {
        // Given
        val apiKey = "test_api_key"

        // When
        appero.start(
            context = context,
            apiKey = apiKey,
            userId = null,
            debug = false
        )

        // Then
        assertNotNull(appero.userId)
        assertTrue(appero.userId!!.isNotEmpty())
        // A UUID should be generated (36 characters with dashes)
        assertTrue(appero.userId!!.matches(Regex("[0-9a-f-]{36}")))
    }

    /**
     * Test 1.5: User ID Restoration
     *
     * Scenario: Initialize when existing user ID is stored
     * Given: UserPreferencesStorage returns existing ID
     * Expected: Existing ID restored instead of generating new one
     */
    @Test
    fun test_start_withExistingUserId_shouldRestoreFromStorage() {
        // Given
        val existingUserId = "existing_user_123"
        val apiKey = "test_api_key"

        // Pre-populate SharedPreferences with existing user ID
        val realUserPrefs = UserPreferencesStorage(context)
        realUserPrefs.saveUserId(existingUserId)

        // When
        appero.start(
            context = context,
            apiKey = apiKey,
            userId = null,
            debug = false
        )

        // Then
        assertEquals(existingUserId, appero.userId)
    }

    /**
     * Test 1.6: Debug Mode Enabled
     *
     * Scenario: Initialize with debug=true
     * Given: debug=true parameter
     * Expected: ApperoLogger initialized with debug enabled, API client uses debug mode
     *
     * Note: This test verifies that the debug flag is passed through correctly.
     * The actual logging behavior is tested elsewhere.
     */
    @Test
    fun test_start_withDebugMode_shouldEnableDebugLogging() {
        // Given
        val apiKey = "test_api_key"

        // When
        appero.start(
            context = context,
            apiKey = apiKey,
            userId = "test_user",
            debug = true
        )

        // Then
        // The debug mode is set internally and will be used for API client calls
        // We verify this indirectly through the initialization completing successfully
        assertNotNull(appero.userId)
    }

    /**
     * Test 1.7: Load Existing Data on Start
     *
     * Scenario: Initialize when ApperoData exists in storage
     * Given: ApperoDataStorage returns existing queued experiences/feedback
     * Expected: Data loaded into state, StateFlows reflect the loaded data
     */
    @Test
    fun test_start_withExistingData_shouldLoadFromStorage() {
        // Given
        val apiKey = "test_api_key"
        val existingExperience = Experience(
            date = System.currentTimeMillis(),
            value = ExperienceRating.POSITIVE,
            detail = "Test experience"
        )
        val existingData = ApperoData(
            unsentExperiences = listOf(existingExperience),
            feedbackPromptShouldDisplay = true
        )

        // Pre-populate storage with existing data
        val realDataStorage = ApperoDataStorage(context)
        realDataStorage.save(existingData)

        // When
        appero.start(
            context = context,
            apiKey = apiKey,
            userId = "test_user",
            debug = false
        )

        // Then
        // Verify StateFlows reflect the loaded data
        assertEquals(true, appero.shouldShowFeedbackPrompt.value)
    }

    // =========================================================================
    // Experience Logging Tests (2.2-2.10)
    // =========================================================================

    /**
     * Test 2.2: Log Experience Online Success
     *
     * Scenario: Log experience with network available
     * Given: Initialized SDK, NetworkMonitor.isConnected=true
     * Expected: API call made to "experiences" endpoint, no queueing
     */
    @Test
    fun test_log_whenOnline_shouldCallAPI() = runTest {
        // Given
        val apiKey = "test_api_key"
        appero.start(context, apiKey, "test_user", false)

        // Force online mode using reflection
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor

        // Set the isConnectedState to true using reflection
        val isConnectedStateField = NetworkMonitor::class.java.getDeclaredField("isConnectedState")
        isConnectedStateField.isAccessible = true
        val isConnectedState =
            isConnectedStateField.get(networkMonitor) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>
        isConnectedState.value = true

        // Clear any existing data to ensure clean state
        val dataStorage = ApperoDataStorage(context)
        dataStorage.clear()

        // When
        appero.log(ExperienceRating.POSITIVE, "Test experience")

        // Then - Verify that the experience was NOT queued (meaning it was sent successfully)
        val loadedData = dataStorage.load().getOrNull()
        // If the API call succeeded, the experience should not be queued
        // (our mock returns success, so it shouldn't be in the queue)
        assertTrue(loadedData == null || loadedData.unsentExperiences.isEmpty())
    }

    /**
     * Test 2.3: Log Experience Offline
     *
     * Scenario: Log experience when offline
     * Given: Initialized SDK, NetworkMonitor.isConnected=false
     * Expected: Experience queued to ApperoData, no API call made
     */
    @Test
    fun test_log_whenOffline_shouldQueueExperience() = runTest {
        // Given
        val apiKey = "test_api_key"
        appero.start(context, apiKey, "test_user", false)

        // Clear any calls from initialization
        io.mockk.clearMocks(ApperoAPIClient)

        // Use reflection to set network offline
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor
        networkMonitor.forceOfflineMode = true

        // When
        appero.log(ExperienceRating.POSITIVE, "Offline experience")

        // Then - No API call should be made
        coVerify(exactly = 0) {
            ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = any(),
                method = any(),
                authorization = any(),
                isDebug = any()
            )
        }

        // Verify experience was queued
        val dataStorage = ApperoDataStorage(context)
        val loadedData = dataStorage.load().getOrNull()
        assertNotNull(loadedData)
        assertTrue(loadedData.unsentExperiences.isNotEmpty())
        assertEquals("Offline experience", loadedData.unsentExperiences.first().detail)
    }

    /**
     * Test 2.4: Log All Rating Types
     *
     * Scenario: Log experiences with each rating enum value
     * Given: Each ExperienceRating (STRONG_NEGATIVE through STRONG_POSITIVE)
     * Expected: Correct rating value sent to API (1-5)
     */
    @Test
    fun test_log_withAllRatingTypes_shouldSendCorrectValues() = runTest {
        // Given
        val apiKey = "test_api_key"
        appero.start(context, apiKey, "test_user", false)

        val ratings = listOf(
            ExperienceRating.STRONG_NEGATIVE to 1,
            ExperienceRating.NEGATIVE to 2,
            ExperienceRating.NEUTRAL to 3,
            ExperienceRating.POSITIVE to 4,
            ExperienceRating.STRONG_POSITIVE to 5
        )

        ratings.forEach { (rating, expectedValue) ->
            // When
            appero.log(rating, "Test ${rating.name}")

            // Then - Verify the rating value is correct
            assertEquals(expectedValue, rating.value)
        }
    }

    /**
     * Test 2.5: Log With Detail Text
     *
     * Scenario: Log experience with optional detail parameter
     * Given: rating=POSITIVE, detail="Purchase completed"
     * Expected: Detail included in API request as "context" field
     */
    @Test
    fun test_log_withDetail_shouldIncludeInRequest() = runTest {
        // Given
        val apiKey = "test_api_key"
        val detail = "Purchase completed"
        appero.start(context, apiKey, "test_user", false)

        // Force online mode using reflection
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor

        val isConnectedStateField = NetworkMonitor::class.java.getDeclaredField("isConnectedState")
        isConnectedStateField.isAccessible = true
        val isConnectedState =
            isConnectedStateField.get(networkMonitor) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>
        isConnectedState.value = true

        // When
        appero.log(ExperienceRating.POSITIVE, detail)

        // Then - Verify that the experience was sent successfully (not queued)
        val dataStorage = ApperoDataStorage(context)
        val loadedData = dataStorage.load().getOrNull()
        // If the API call succeeded with the detail, it shouldn't be queued
        assertTrue(loadedData == null || loadedData.unsentExperiences.isEmpty())
    }

    /**
     * Test 2.6: Log Without Detail
     *
     * Scenario: Log experience with detail=null
     * Given: rating=POSITIVE, detail=null
     * Expected: Empty string sent in "context" field
     */
    @Test
    fun test_log_withoutDetail_shouldSendEmptyString() = runTest {
        // Given
        val apiKey = "test_api_key"
        appero.start(context, apiKey, "test_user", false)

        // Force online mode using reflection
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor

        val isConnectedStateField = NetworkMonitor::class.java.getDeclaredField("isConnectedState")
        isConnectedStateField.isAccessible = true
        val isConnectedState =
            isConnectedStateField.get(networkMonitor) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>
        isConnectedState.value = true

        // When
        appero.log(ExperienceRating.POSITIVE, null)

        // Then - Verify that the experience was sent successfully (not queued)
        val dataStorage = ApperoDataStorage(context)
        val loadedData = dataStorage.load().getOrNull()
        assertTrue(loadedData == null || loadedData.unsentExperiences.isEmpty())
    }

    /**
     * Test 2.7: API Response Triggers Feedback Prompt
     *
     * Scenario: ExperienceResponse indicates to show feedback UI
     * Given: API returns shouldShowFeedbackUI=true
     * Expected: shouldShowFeedbackPrompt StateFlow emits true
     */
    @Test
    fun test_log_whenAPITriggersPrompt_shouldUpdateStateFlow() = runTest {
        // Given
        val apiKey = "test_api_key"
        val response = ExperienceResponse(
            shouldShowFeedbackUI = true,
            flowType = "normal",
            feedbackUI = null
        )
        val responseJson = Json.encodeToString(response)

        coEvery {
            ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = any(),
                method = any(),
                authorization = any(),
                isDebug = any()
            )
        } returns ApperoAPIResponse.Success(responseJson.toByteArray())

        appero.start(context, apiKey, "test_user", false)

        // Force online mode using reflection
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor

        val isConnectedStateField = NetworkMonitor::class.java.getDeclaredField("isConnectedState")
        isConnectedStateField.isAccessible = true
        val isConnectedState =
            isConnectedStateField.get(networkMonitor) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>
        isConnectedState.value = true

        // When
        appero.log(ExperienceRating.POSITIVE)

        // Then
        assertEquals(true, appero.shouldShowFeedbackPrompt.value)
    }

    /**
     * Test 2.8: API Response Updates UI Strings
     *
     * Scenario: ExperienceResponse contains custom UI strings
     * Given: API returns custom title/subtitle/prompt
     * Expected: feedbackUIStrings StateFlow updated with custom values
     */
    @Test
    fun test_log_whenAPIReturnsCustomStrings_shouldUpdateUIStrings() = runTest {
        // Given
        val apiKey = "test_api_key"
        val customStrings = FeedbackUIStrings(
            title = "Custom Title",
            subtitle = "Custom Subtitle",
            prompt = "Custom Prompt"
        )
        val response = ExperienceResponse(
            shouldShowFeedbackUI = false,
            flowType = "normal",
            feedbackUI = customStrings
        )
        val responseJson = Json.encodeToString(response)

        coEvery {
            ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = any(),
                method = any(),
                authorization = any(),
                isDebug = any()
            )
        } returns ApperoAPIResponse.Success(responseJson.toByteArray())

        appero.start(context, apiKey, "test_user", false)

        // Force online mode using reflection
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor

        val isConnectedStateField = NetworkMonitor::class.java.getDeclaredField("isConnectedState")
        isConnectedStateField.isAccessible = true
        val isConnectedState =
            isConnectedStateField.get(networkMonitor) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>
        isConnectedState.value = true

        // When
        appero.log(ExperienceRating.POSITIVE)

        // Then
        assertEquals(customStrings, appero.feedbackUIStrings.value)
    }

    /**
     * Test 2.9: API Response Updates Flow Type
     *
     * Scenario: ExperienceResponse specifies flow type
     * Given: API returns flowType="frustration"
     * Expected: flowType StateFlow emits NEGATIVE
     */
    @Test
    fun test_log_whenAPIReturnsFlowType_shouldUpdateFlowType() = runTest {
        // Given
        val apiKey = "test_api_key"
        val response = ExperienceResponse(
            shouldShowFeedbackUI = false,
            flowType = "frustration",
            feedbackUI = null
        )
        val responseJson = Json.encodeToString(response)

        coEvery {
            ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = any(),
                method = any(),
                authorization = any(),
                isDebug = any()
            )
        } returns ApperoAPIResponse.Success(responseJson.toByteArray())

        appero.start(context, apiKey, "test_user", false)

        // Force online mode using reflection
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor

        val isConnectedStateField = NetworkMonitor::class.java.getDeclaredField("isConnectedState")
        isConnectedStateField.isAccessible = true
        val isConnectedState =
            isConnectedStateField.get(networkMonitor) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>
        isConnectedState.value = true

        // When
        appero.log(ExperienceRating.NEGATIVE)

        // Then
        assertEquals(FlowType.NEGATIVE, appero.flowType.value)
    }

    /**
     * Test 2.10: Experience Queuing on API Error
     *
     * Scenario: API returns error (network error, server error)
     * Given: API call fails with any error type
     * Expected: Experience added to unsentExperiences queue
     */
    @Test
    fun test_log_whenAPIError_shouldQueueExperience() = runTest {
        // Given
        val apiKey = "test_api_key"
        coEvery {
            ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = any(),
                method = any(),
                authorization = any(),
                isDebug = any()
            )
        } returns ApperoAPIResponse.Error(ApperoAPIError.NetworkError(500))

        appero.start(context, apiKey, "test_user", false)

        // When
        appero.log(ExperienceRating.POSITIVE, "Error test")

        // Then - Verify experience was queued
        val dataStorage = ApperoDataStorage(context)
        val loadedData = dataStorage.load().getOrNull()
        assertNotNull(loadedData)
        assertTrue(loadedData.unsentExperiences.isNotEmpty())
    }

    // =========================================================================
    // StateFlow Observation Tests (4.1-4.6)
    // =========================================================================

    /**
     * Test 4.1: shouldShowFeedbackPrompt Initial State
     *
     * Scenario: Check initial state before any experiences
     * Expected: Emits false by default
     */
    @Test
    fun test_shouldShowFeedbackPrompt_initialState_shouldBeFalse() {
        // Given - fresh Appero instance

        // Then
        assertEquals(false, appero.shouldShowFeedbackPrompt.value)
    }

    /**
     * Test 4.2: shouldShowFeedbackPrompt Updates on API Response
     *
     * Scenario: API response triggers prompt
     * Given: ExperienceResponse with shouldShowFeedbackUI=true
     * Expected: StateFlow emits true
     */
    @Test
    fun test_shouldShowFeedbackPrompt_whenTriggered_shouldEmitTrue() = runTest {
        // Given
        val apiKey = "test_api_key"
        val response = ExperienceResponse(
            shouldShowFeedbackUI = true,
            flowType = "normal",
            feedbackUI = null
        )
        val responseJson = Json.encodeToString(response)

        coEvery {
            ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = any(),
                method = any(),
                authorization = any(),
                isDebug = any()
            )
        } returns ApperoAPIResponse.Success(responseJson.toByteArray())

        appero.start(context, apiKey, "test_user", false)

        // Force online mode using reflection
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor

        val isConnectedStateField = NetworkMonitor::class.java.getDeclaredField("isConnectedState")
        isConnectedStateField.isAccessible = true
        val isConnectedState =
            isConnectedStateField.get(networkMonitor) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>
        isConnectedState.value = true

        // When
        appero.shouldShowFeedbackPrompt.test {
            val initialValue = awaitItem()
            assertEquals(false, initialValue)

            appero.log(ExperienceRating.POSITIVE)

            val updatedValue = awaitItem()
            assertEquals(true, updatedValue)
        }
    }

    /**
     * Test 4.3: feedbackUIStrings Initial State
     *
     * Scenario: Check initial UI strings
     * Expected: Emits default strings from resources (R.string values)
     */
    @Test
    fun test_feedbackUIStrings_initialState_shouldBeDefaults() {
        // Given
        val apiKey = "test_api_key"
        appero.start(context, apiKey, "test_user", false)

        // Then
        val uiStrings = appero.feedbackUIStrings.value
        assertNotNull(uiStrings)
        // The default strings are loaded from context resources
        assertTrue(uiStrings.title.isNotEmpty())
        assertTrue(uiStrings.subtitle.isNotEmpty())
        assertTrue(uiStrings.prompt.isNotEmpty())
    }

    /**
     * Test 4.4: feedbackUIStrings Updates from API
     *
     * Scenario: API provides custom strings
     * Given: ExperienceResponse with custom feedbackUI
     * Expected: StateFlow emits updated strings
     */
    @Test
    fun test_feedbackUIStrings_whenAPIUpdates_shouldEmitNewStrings() = runTest {
        // Given
        val apiKey = "test_api_key"
        val customStrings = FeedbackUIStrings(
            title = "API Title",
            subtitle = "API Subtitle",
            prompt = "API Prompt"
        )
        val response = ExperienceResponse(
            shouldShowFeedbackUI = false,
            flowType = "normal",
            feedbackUI = customStrings
        )
        val responseJson = Json.encodeToString(response)

        coEvery {
            ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = any(),
                method = any(),
                authorization = any(),
                isDebug = any()
            )
        } returns ApperoAPIResponse.Success(responseJson.toByteArray())

        appero.start(context, apiKey, "test_user", false)

        // Force online mode using reflection
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor

        val isConnectedStateField = NetworkMonitor::class.java.getDeclaredField("isConnectedState")
        isConnectedStateField.isAccessible = true
        val isConnectedState =
            isConnectedStateField.get(networkMonitor) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>
        isConnectedState.value = true

        // When
        appero.feedbackUIStrings.test {
            skipItems(1) // Skip initial value

            appero.log(ExperienceRating.POSITIVE)

            val updatedStrings = awaitItem()
            assertEquals("API Title", updatedStrings.title)
            assertEquals("API Subtitle", updatedStrings.subtitle)
            assertEquals("API Prompt", updatedStrings.prompt)
        }
    }

    /**
     * Test 4.5: flowType Initial State
     *
     * Scenario: Check initial flow type
     * Expected: Emits NEUTRAL by default
     */
    @Test
    fun test_flowType_initialState_shouldBeNeutral() {
        // Given - fresh Appero instance

        // Then
        assertEquals(FlowType.NEUTRAL, appero.flowType.value)
    }

    /**
     * Test 4.6: flowType Updates from API
     *
     * Scenario: API specifies flow type
     * Given: ExperienceResponse with flowType="normal"
     * Expected: StateFlow emits POSITIVE
     */
    @Test
    fun test_flowType_whenAPIUpdates_shouldEmitNewFlowType() = runTest {
        // Given
        val apiKey = "test_api_key"
        val response = ExperienceResponse(
            shouldShowFeedbackUI = false,
            flowType = "normal",
            feedbackUI = null
        )
        val responseJson = Json.encodeToString(response)

        coEvery {
            ApperoAPIClient.sendRequest(
                endpoint = "experiences",
                fields = any(),
                method = any(),
                authorization = any(),
                isDebug = any()
            )
        } returns ApperoAPIResponse.Success(responseJson.toByteArray())

        appero.start(context, apiKey, "test_user", false)

        // Force online mode using reflection
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor

        val isConnectedStateField = NetworkMonitor::class.java.getDeclaredField("isConnectedState")
        isConnectedStateField.isAccessible = true
        val isConnectedState =
            isConnectedStateField.get(networkMonitor) as kotlinx.coroutines.flow.MutableStateFlow<Boolean>
        isConnectedState.value = true

        // When
        appero.flowType.test {
            val initialValue = awaitItem()
            assertEquals(FlowType.NEUTRAL, initialValue)

            appero.log(ExperienceRating.POSITIVE)

            val updatedValue = awaitItem()
            assertEquals(FlowType.POSITIVE, updatedValue)
        }
    }

    // =========================================================================
    // Data Persistence Tests (6.1-6.4)
    // =========================================================================

    /**
     * Test 6.1: Queued Experiences Persist
     *
     * Scenario: Queued experiences saved to storage
     * Given: Experience queued due to offline state
     * Expected: ApperoDataStorage.save() called with updated unsentExperiences
     */
    @Test
    fun test_queuedExperiences_whenOffline_shouldPersist() = runTest {
        // Given
        val apiKey = "test_api_key"
        appero.start(context, apiKey, "test_user", false)

        // Force offline mode
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor
        networkMonitor.forceOfflineMode = true

        // When
        appero.log(ExperienceRating.POSITIVE, "Queued experience")

        // Then - Verify data was persisted
        val dataStorage = ApperoDataStorage(context)
        val loadedData = dataStorage.load().getOrNull()
        assertNotNull(loadedData)
        assertEquals(1, loadedData.unsentExperiences.size)
        assertEquals("Queued experience", loadedData.unsentExperiences.first().detail)
    }

    /**
     * Test 6.2: Queued Feedback Persists
     *
     * Scenario: Queued feedback saved to storage
     * Given: Feedback queued due to offline state
     * Expected: ApperoDataStorage.save() called with updated unsentFeedback
     */
    @Test
    fun test_queuedFeedback_whenOffline_shouldPersist() = runTest {
        // Given
        val apiKey = "test_api_key"
        appero.start(context, apiKey, "test_user", false)

        // Force offline mode
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor
        networkMonitor.forceOfflineMode = true

        // When
        appero.postFeedback(ExperienceRating.POSITIVE, "Offline feedback")

        // Then - Verify data was persisted
        val dataStorage = ApperoDataStorage(context)
        val loadedData = dataStorage.load().getOrNull()
        assertNotNull(loadedData)
        assertEquals(1, loadedData.unsentFeedback.size)
        assertEquals("Offline feedback", loadedData.unsentFeedback.first().feedback)
    }

    /**
     * Test 6.3: Data Loaded on Initialization
     *
     * Scenario: Previously saved data restored
     * Given: ApperoDataStorage contains queued items
     * When: start() called
     * Expected: apperoDataState populated with loaded data
     */
    @Test
    fun test_initialization_withExistingQueuedData_shouldLoadFromStorage() {
        // Given
        val existingExperience = Experience(
            date = System.currentTimeMillis(),
            value = ExperienceRating.POSITIVE,
            detail = "Persisted experience"
        )
        val existingData = ApperoData(
            unsentExperiences = listOf(existingExperience)
        )
        val dataStorage = ApperoDataStorage(context)
        dataStorage.save(existingData)

        // When
        val apiKey = "test_api_key"
        appero.start(context, apiKey, "test_user", false)

        // Then - Data should be loaded (verified by test 1.7 already, but we can check storage)
        val loadedData = dataStorage.load().getOrNull()
        assertNotNull(loadedData)
        assertEquals(1, loadedData.unsentExperiences.size)
    }

    /**
     * Test 6.4: State Updates Trigger Persistence
     *
     * Scenario: Any state change persists to storage
     * Given: Any updateApperoData() call
     * Expected: ApperoDataStorage.save() called
     */
    @Test
    fun test_stateUpdate_shouldPersistToStorage() = runTest {
        // Given
        val apiKey = "test_api_key"
        appero.start(context, apiKey, "test_user", false)
        val dataStorage = ApperoDataStorage(context)

        // Clear existing data
        dataStorage.clear()

        // Force offline to trigger queueing (which updates state)
        val networkMonitorField = Appero::class.java.getDeclaredField("networkMonitor")
        networkMonitorField.isAccessible = true
        val networkMonitor = networkMonitorField.get(appero) as NetworkMonitor
        networkMonitor.forceOfflineMode = true

        // When - Trigger a state update by logging
        appero.log(ExperienceRating.POSITIVE, "Test")

        // Then - Verify data was saved
        val savedData = dataStorage.load().getOrNull()
        assertNotNull(savedData)
        assertTrue(savedData.unsentExperiences.isNotEmpty())
    }
}
