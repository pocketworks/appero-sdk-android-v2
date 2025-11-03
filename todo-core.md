# Phase 1: Core Infrastructure Implementation Plan

## Overview

This plan details the implementation of Phase 1: Core Infrastructure, covering all foundational components (1.1-1.8) needed before building the UI layer. These components form the backbone of the SDK and must be completed before moving to networking and UI phases.

## Implementation Order

The components should be implemented in the following order to manage dependencies:

1. Utilities & Debug (1.8) - Foundation for logging
2. Data Models (1.2) - Foundation for all other components
3. Storage & Persistence (1.4) - Needed by singleton
4. Network Monitoring (1.5) - Needed by singleton
5. API Client (1.3) - Needed by singleton for networking
6. Analytics Interface (1.7) - Simple interface, needed by singleton
7. Retry Mechanism (1.6) - Depends on storage, network, API client
8. Main Appero Singleton (1.1) - Orchestrates all above components

## Component 1.8: Utilities & Debug

### Files to Create
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/util/ApperoDebug.kt`

### Implementation Details
- Internal object for debug logging
- `isDebug` flag check before logging
- Use Android `Log.d()` with `[Appero]` tag prefix
- Static logging function: `log(message: String)`
- Follow Android logging best practices

### Dependencies
- None (uses Android Log API)

### KDoc Requirements
- Internal documentation (not public API)

## Component 1.2: Data Models

### Files to Create
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/model/ExperienceRating.kt`
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/model/Experience.kt`
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/model/QueuedFeedback.kt`
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/model/FlowType.kt`
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/model/FeedbackUIStrings.kt`
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/model/ApperoData.kt`
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/model/ExperienceResponse.kt`

### Implementation Details

**ExperienceRating (enum class):**
- Values: STRONG_POSITIVE(5), POSITIVE(4), NEUTRAL(3), NEGATIVE(2), STRONG_NEGATIVE(1)
- Public enum for API
- JSON serializable

**Experience (data class):**
- Properties: `date: Instant`, `value: ExperienceRating`, `context: String?`
- Internal visibility (used within SDK)
- JSON serializable with ISO8601 date format

**QueuedFeedback (data class):**
- Properties: `date: Instant`, `rating: Int`, `feedback: String?`
- Internal visibility
- JSON serializable

**FlowType (enum class):**
- Values: POSITIVE("normal"), NEUTRAL("neutral"), NEGATIVE("frustration")
- JSON serializable with custom string values

**FeedbackUIStrings (data class):**
- Properties: `title: String`, `subtitle: String`, `prompt: String`
- Public (exposed in API)
- JSON serializable

**ApperoData (data class):**
- Properties:
  - `unsentExperiences: List<Experience>`
  - `unsentFeedback: List<QueuedFeedback>`
  - `feedbackPromptShouldDisplay: Boolean`
  - `feedbackUIStrings: FeedbackUIStrings`
  - `lastPromptDate: Instant?`
  - `flowType: FlowType`
- Internal visibility
- JSON serializable
- Default values in companion object

**ExperienceResponse (data class):**
- Properties:
  - `shouldShowFeedbackUI: Boolean`
  - `flowType: FlowType`
  - `feedbackUI: FeedbackUIStrings?`
- Internal visibility
- JSON serializable with custom field names (@SerialName)

### Serialization Strategy
- Use kotlinx.serialization for JSON
- Add `@Serializable` annotations
- Use `@SerialName` for API field name mapping
- ISO8601 date handling via custom serializer or Instant

### Dependencies to Add
- kotlinx-serialization-json
- kotlinx-serialization-core

### KDoc Requirements
- Public enums and data classes need KDoc
- Document each property's purpose

## Component 1.4: Storage & Persistence

### Files to Create
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/storage/UserPreferences.kt`
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/storage/ApperoDataStorage.kt`

### Implementation Details

**UserPreferences:**
- Internal class wrapping SharedPreferences
- Methods:
  - `getUserId(): String?` - Retrieve stored user ID
  - `saveUserId(userId: String)` - Save user ID
  - `clearUserId()` - Remove user ID (for reset)
- Key: `"appero_user_id"` (constant)
- Context required for SharedPreferences access

**ApperoDataStorage:**
- Internal class for ApperoData persistence via SharedPreferences
- **Storage Strategy:** Store ApperoData as JSON string in SharedPreferences (not file system)
- Methods:
  - `save(data: ApperoData, context: Context): Result<Unit>` - Save ApperoData as JSON string
  - `load(context: Context): Result<ApperoData>` - Load and deserialize ApperoData from JSON string
  - `clear(context: Context): Result<Unit>` - Remove ApperoData from SharedPreferences
  - `getPreferences(context: Context): SharedPreferences` - Get SharedPreferences instance (helper)
- SharedPreferences key: `"appero_data_json"` (constant)
- Implementation details:
  - Serialize ApperoData to JSON string using kotlinx.serialization
  - Store JSON string in SharedPreferences as String value
  - Load JSON string from SharedPreferences and deserialize to ApperoData
  - Use atomic SharedPreferences commits (apply() for async, commit() for sync if needed)
  - Error handling with Result<T> type
  - Default ApperoData creation if SharedPreferences key doesn't exist or is null
  - Log errors via ApperoDebug
  - Handle JSON parsing errors gracefully

### Error Handling
- Use Kotlin `Result<T>` for type-safe error handling
- Catch and log JSON parsing errors (corrupted data)
- Catch and log SharedPreferences I/O errors (if any)
- Return default ApperoData if SharedPreferences key is missing or contains invalid JSON

### Dependencies
- Uses kotlinx.serialization (already added for models)
- Android Context API
- SharedPreferences API

### Advantages of SharedPreferences over File System
- Atomic operations built-in
- Simpler implementation (no file path management)
- Better performance for small data
- Automatic backup support (if enabled in app)
- Less file system clutter

### KDoc Requirements
- Internal classes, document method purposes
- Document SharedPreferences key constants

## Component 1.5: Network Monitoring

### Files to Create
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/network/NetworkMonitor.kt`

### Implementation Details
- Internal class for connectivity monitoring
- Constructor: `NetworkMonitor(context: Context)`
- Properties:
  - `isConnected: StateFlow<Boolean>` - Reactive connectivity state
  - `forceOfflineMode: Boolean` - For testing (default false)
- Implementation:
  - Use `ConnectivityManager` to monitor network state
  - Use `NetworkCallback` for reactive updates
  - Convert callbacks to Flow using `callbackFlow`
  - Handle API level differences (pre-Android N vs post)
  - Default to connected if unable to determine
- Lifecycle:
  - Register callback on creation
  - Unregister in cleanup method
  - Handle permission requirements gracefully

### API Compatibility
- Check Android version for NetworkCallback API availability
- Provide fallback for older versions if needed

### Dependencies to Add
- androidx.lifecycle:lifecycle-runtime-ktx (for ProcessLifecycleOwner if needed)
- Or use Application-level monitoring

### KDoc Requirements
- Document StateFlow behavior
- Document forceOfflineMode testing usage

## Component 1.3: API Client

### Files to Create
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/api/ApperoAPIError.kt`
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/api/ApperoAPIClient.kt`

### Implementation Details

**ApperoAPIError (sealed class):**
- Sealed class hierarchy for type-safe error handling:
  - `NoData` - Empty response
  - `NetworkError(val statusCode: Int)` - HTTP error status
  - `ServerMessage(val response: ApperoErrorResponse?)` - 401/422 with error details
  - `NoResponse` - No HTTP response received
  - `UnknownError(val throwable: Throwable)` - Unexpected errors
- Internal visibility

**ApperoErrorResponse (data class):**
- Nested data class for server error responses
- Properties: `error: String?`, `message: String?`, `details: ErrorDetails?`
- JSON deserializable

**ApperoAPIClient (object):**
- Static-style API using Kotlin object
- **HTTP Client: Use Ktor Client** (confirmed choice)
- Base URL: `https://app.appero.co.uk/api/v1` (constant)
- Methods:
  - `suspend fun sendRequest(endpoint: String, fields: Map<String, Any>, method: HttpMethod, authorization: String): Result<ByteArray>`
- HTTP methods enum: GET, POST, PUT, PATCH, DELETE
- Implementation:
  - Use Ktor Client for HTTP requests
  - Configure Ktor client with:
    - Android engine
    - JSON content negotiation
    - 10-second timeout
    - Logging interceptor (optional, for debugging)
  - Bearer token authentication via Authorization header
  - JSON request body encoding using kotlinx.serialization
  - JSON response parsing
  - Handle HTTP status codes (200-204 success, 401/422 error details, others network error)
  - Return Result<ByteArray> for type-safe error handling
  - Logging via ApperoDebug for requests/responses
- Ktor Client setup:
  - Create singleton HttpClient instance
  - Use `ContentNegotiation` plugin with `Json` serializer
  - Configure request timeout
  - Set base URL or construct full URL per request

### HTTP Client Implementation with Ktor

**Ktor Client Configuration:**
```kotlin
val httpClient = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        })
    }
    engine {
        connectTimeout = 10_000
        socketTimeout = 10_000
    }
    // Optional: Logging
    if (isDebug) {
        install(Logging) {
            level = LogLevel.INFO
        }
    }
}
```

**Request Building:**
- Use `httpClient.post/put/get/etc()` methods
- Build URL with base URL + endpoint
- Set Authorization header: `"Bearer $authorization"`
- Set Content-Type header: `"application/json"`
- Serialize request body to JSON string
- Parse response body

### Dependencies to Add
- io.ktor:ktor-client-core
- io.ktor:ktor-client-android
- io.ktor:ktor-client-content-negotiation
- io.ktor:ktor-serialization-kotlinx-json
- io.ktor:ktor-client-logging (optional, for debugging)

### KDoc Requirements
- Document suspend function behavior
- Document error types and when they occur
- Document Ktor client configuration

## Component 1.7: Analytics Interface

### Files to Create
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/analytics/IApperoAnalytics.kt`

### Implementation Details
- Public interface for analytics integration
- Methods:
  - `fun logApperoFeedback(rating: Int, feedback: String)`
  - `fun logRatingSelected(rating: Int)`
- Simple interface, no default implementations
- Public API (consumers implement this)

### KDoc Requirements
- Full KDoc for public interface
- Document when each method is called
- Provide usage examples in KDoc

## Component 1.6: Retry Mechanism

### Files to Create
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/RetryManager.kt` (or integrate into Appero class)

### Implementation Details
- Internal class or part of Appero singleton
- Responsibilities:
  - Periodic retry every 3 minutes (180 seconds)
  - Process unsentExperiences queue
  - Process unsentFeedback queue
  - Remove successfully sent items
  - Update storage after processing
- Implementation:
  - Use Kotlin Coroutines `flow` with `onEach` and `delay(180_000)`
  - Or use `Timer` in coroutine scope
  - Run on `Dispatchers.IO`
  - Check network connectivity before attempting retry
  - Respect `forceOfflineMode` flag
  - Stop retry timer on cleanup
- Dependencies:
  - Requires ApperoDataStorage
  - Requires NetworkMonitor
  - Requires ApperoAPIClient
  - Requires Appero singleton state

### Lifecycle Management
- Start retry mechanism after Appero initialization
- Stop retry mechanism on cleanup/reset

### KDoc Requirements
- Document retry interval
- Document queue processing behavior

## Component 1.1: Main Appero Singleton

### Files to Create
- `main/src/main/java/uk/co/pocketworks/appero/sdk/main/Appero.kt`

### Implementation Details

**Class Structure:**
- Kotlin `object` for singleton pattern
- Public API class

**Properties:**
- `instance: Appero` (via object, public)
- `var apiKey: String?` (private, set on start)
- `var userId: String?` (public, can be updated)
- `var isDebug: Boolean = false` (public)
- `var analyticsDelegate: IApperoAnalytics?` (public, nullable)
- `val shouldShowFeedbackPrompt: StateFlow<Boolean>` (public, reactive state)
- `val feedbackUIStrings: StateFlow<FeedbackUIStrings>` (public)
- `val flowType: StateFlow<FlowType>` (public)

**Internal Properties:**
- NetworkMonitor instance
- ApperoDataStorage instance
- UserPreferences instance
- CoroutineScope for background work
- Retry mechanism job
- Ktor HttpClient instance (for API calls)

**Methods:**

**Initialization:**
- `fun start(context: Context, apiKey: String, userId: String? = null)` - Initialize SDK
  - Validate API key not empty
  - Generate or restore user ID if not provided
  - Initialize storage components
  - Initialize network monitoring
  - Initialize Ktor HttpClient
  - Start retry mechanism
  - Load existing ApperoData from SharedPreferences

**Experience Logging:**
- `fun log(experience: ExperienceRating, context: String? = null)` - Log user experience
  - Create Experience record
  - Check network connectivity
  - Post immediately if online, else queue
  - Handle API response to update shouldShowFeedbackPrompt
  - Save updated ApperoData to SharedPreferences

**Feedback Submission:**
- `suspend fun postFeedback(rating: Int, feedback: String?): Result<Boolean>` - Submit feedback
  - Validate rating (1-5) and feedback length (<500 chars)
  - Create QueuedFeedback
  - Post immediately if online, else queue
  - Save updated ApperoData to SharedPreferences
  - Return success/failure result

**State Management:**
- `fun dismissApperoPrompt()` - Dismiss prompt and prevent re-show
- `fun reset(context: Context)` - Clear all data (user ID, ApperoData from SharedPreferences)

**Internal Methods:**
- `private suspend fun postExperience(experience: Experience)` - Send experience to API using Ktor
- `private fun queueExperience(experience: Experience)` - Add to queue and save to SharedPreferences
- `private suspend fun processUnsentExperiences()` - Retry queued experiences
- `private suspend fun processUnsentFeedback()` - Retry queued feedback
- `private fun handleExperienceResponse(response: ExperienceResponse)` - Process API response
- `private fun updateApperoData(update: (ApperoData) -> ApperoData)` - Helper to update and persist ApperoData

**Persistence Integration:**
- Load ApperoData from SharedPreferences on initialization
- Save ApperoData to SharedPreferences whenever it changes (after queueing, after processing queues, after API responses)
- Use atomic SharedPreferences operations

**Network Integration:**
- Observe NetworkMonitor.isConnected
- Use connectivity state for queueing decisions

**Constants:**
- `kApperoFeedbackPromptNotification: String` - Notification name for XML-based apps (equivalent to iOS notification)

### StateFlow Usage
- Use StateFlow for reactive state that Compose can observe
- Initial values from loaded ApperoData or defaults
- Update StateFlow when API responses received
- Ensure StateFlow updates happen on Main dispatcher

### Dependencies
- Requires all previous components (1.2-1.8)
- Android Context for initialization
- CoroutineScope (use Application-scoped or create internal scope)

### KDoc Requirements
- Full public API documentation
- Usage examples in KDoc
- Document StateFlow behavior
- Document thread safety considerations

## Build Configuration Updates

### Required Dependencies (libs.versions.toml)

Add to `[versions]`:
```
kotlinxSerialization = "1.6.0"
ktor = "2.3.5"
coroutines = "1.7.3"
lifecycle = "2.7.0"
```

Add to `[libraries]`:
```
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }
kotlinx-serialization-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-core", version.ref = "kotlinxSerialization" }
kotlinx-coroutines-core = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "coroutines" }
kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
ktor-client-core = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }
ktor-client-android = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }
ktor-client-content-negotiation = { group = "io.ktor", name = "ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { group = "io.ktor", name = "ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-client-logging = { group = "io.ktor", name = "ktor-client-logging", version.ref = "ktor" }
lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
```

Add to `[plugins]`:
```
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

### Build File Updates (main/build.gradle.kts)

**Add plugin:**
```kotlin
plugins {
    // ... existing plugins
    alias(libs.plugins.kotlin.serialization)
}
```

**Update dependencies:**
```kotlin
dependencies {
    // ... existing dependencies
    
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.core)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    
    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    
    // Lifecycle (for network monitoring if needed)
    implementation(libs.lifecycle.runtime.ktx)
}
```

## Testing Strategy

### Unit Tests to Create (Phase 1)

**ApperoDebug Tests:**
- Test logging only occurs when isDebug = true
- Test log format includes [Appero] prefix

**Data Model Tests:**
- Test JSON serialization/deserialization
- Test enum value mappings
- Test default values

**Storage Tests:**
- Test SharedPreferences save/load operations for ApperoData
- Test JSON string serialization/deserialization
- Test error handling (missing key, corrupted JSON string)
- Test atomic SharedPreferences operations
- Test default ApperoData creation

**Network Monitor Tests:**
- Mock ConnectivityManager
- Test StateFlow updates
- Test forceOfflineMode

**API Client Tests:**
- Mock Ktor HttpClient
- Test request format (headers, body)
- Test error handling for different status codes
- Test Bearer token inclusion
- Test Ktor client configuration

**Retry Manager Tests:**
- Mock dependencies
- Test retry interval
- Test queue processing logic
- Test network state integration

**Appero Singleton Tests:**
- Test initialization
- Test experience logging (online/offline)
- Test feedback submission
- Test state updates
- Test reset functionality
- Test SharedPreferences persistence

## Implementation Checklist

- [ ] Add build dependencies (kotlinx-serialization, Ktor, Coroutines)
- [ ] Create utilities package and ApperoDebug
- [ ] Create model package and all data classes/enums
- [ ] Implement JSON serialization for all models
- [ ] Create storage package and implementations (UserPreferences + ApperoDataStorage with SharedPreferences)
- [ ] Test storage save/load operations (SharedPreferences-based)
- [ ] Create network package and NetworkMonitor
- [ ] Test network monitoring
- [ ] Create api package and ApperoAPIClient (using Ktor)
- [ ] Configure Ktor client with Android engine and JSON serialization
- [ ] Test API client with mock server
- [ ] Create analytics interface
- [ ] Implement retry mechanism
- [ ] Create main Appero singleton class
- [ ] Wire all components together
- [ ] Implement initialization flow
- [ ] Implement experience logging flow
- [ ] Implement feedback submission flow
- [ ] Implement queue processing
- [ ] Add comprehensive KDoc
- [ ] Write unit tests for all components
- [ ] Test end-to-end flow

## Key Implementation Notes

1. **Error Handling:** Use Kotlin `Result<T>` consistently for operations that can fail
2. **Threading:** All I/O operations on Dispatchers.IO, StateFlow updates on Main
3. **Lifecycle:** Consider Application context usage for long-lived components
4. **Testing:** Mock external dependencies (Context, ConnectivityManager, Ktor HttpClient)
5. **Visibility:** Keep internal implementation details `internal`, expose only public API
6. **State Management:** Use StateFlow for reactive state that UI can observe
7. **Date Handling:** Use `Instant` for dates, format to ISO8601 for JSON
8. **Constants:** Extract magic strings/numbers to companion object constants
9. **SharedPreferences:** Use atomic operations (apply() for async, commit() for sync when needed)
10. **Ktor Client:** Configure once, reuse HttpClient instance throughout SDK lifecycle
