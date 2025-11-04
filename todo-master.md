# Android SDK Implementation Plan

## Overview

This plan outlines the major architectural components needed to implement the Android Appero SDK, mirroring the iOS SDK architecture while leveraging Android-specific technologies (Kotlin, Jetpack Compose, Android Architecture Components).

**Current Status: 85% Complete (17/20 tasks)**
- ✅ Phase 1-3: Core Infrastructure (Complete)
- ✅ Phase 4-5: UI Module (Complete)
- ⏸️ Phase 6-7: Sample App & Testing (Pending)

## Major Components

### 1. Core SDK Module (`main` module) ✅ COMPLETE

**1.1 Main Appero Singleton Class** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.Appero`
- **Purpose:** Central singleton instance managing SDK lifecycle and state
- **Key Responsibilities:**
  - Singleton pattern implementation
  - Initialization (`start(apiKey, userId)`)
  - Experience logging API
  - Feedback submission API
  - State management (shouldShowFeedbackPrompt)
  - Network connectivity monitoring setup
  - Retry timer management
  - Persistence coordination
- **Android Adaptation:**
  - Use Kotlin `object` for singleton or companion object with private constructor
  - Use `Flow` or `StateFlow` for reactive state (equivalent to iOS `@Published`)
  - Integration with Android `ConnectivityManager` for network monitoring
  - Use `CoroutineScope` and `CoroutineTimer` for retry mechanism

**1.2 Data Models** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.model/`
- **Components:**
  - `ExperienceRating` - Enum for rating values (1-5)
  - `Experience` - Data class for logged experiences
  - `QueuedFeedback` - Data class for queued feedback submissions
  - `ApperoData` - Main persistence data class
  - `FeedbackUIStrings` - UI text from backend
  - `FlowType` - Enum for UI flow types (positive/neutral/negative)
  - `ExperienceResponse` - API response model
- **Android Adaptation:**
  - Use `data class` with `@Serializable` (kotlinx.serialization) or Gson
  - Use `enum class` for enums
  - ISO8601 date formatting with Java/Kotlin date APIs

**1.3 API Client** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.api.ApperoAPIClient`
- **Purpose:** Centralized networking layer
- **Key Responsibilities:**
  - HTTP request/response handling
  - Bearer token authentication
  - Error handling and parsing
  - JSON serialization/deserialization
- **Android Adaptation:**
  - Use Kotlin Coroutines with `suspend` functions
  - Use Retrofit or Ktor Client for HTTP
  - Use `Result<T>` or sealed classes for error handling
  - Base URL: `https://app.appero.co.uk/api/v1`
  - Endpoints: `/experiences`, `/feedback`

**1.4 Storage & Persistence** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.storage/`
- **Components:**
  - `ApperoDataStorage` - JSON file management
  - `UserPreferences` - SharedPreferences wrapper for user ID
- **Key Responsibilities:**
  - Save/load `ApperoData` JSON file in internal storage
  - Store user ID in SharedPreferences
  - Atomic write operations
  - Default value handling
- **Android Adaptation:**
  - Use `Context.filesDir` for JSON file location
  - Use `SharedPreferences` for user ID (equivalent to iOS UserDefaults)
  - Use kotlinx.serialization or Gson for JSON
  - File: `ApperoData.json` in app's internal files directory

**1.5 Network Monitoring** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.network/`
- **Components:**
  - `NetworkMonitor` - Connectivity monitoring
- **Key Responsibilities:**
  - Monitor network connectivity status
  - Provide `Flow<Boolean>` for connectivity state
  - Support force offline mode for testing
- **Android Adaptation:**
  - Use `ConnectivityManager` and `NetworkCallback`
  - Convert to Kotlin `Flow` for reactive updates
  - Use `ProcessLifecycleOwner` if needed for lifecycle awareness

**1.6 Retry Mechanism** ✅
- **Location:** Integrated into `Appero` class or separate `RetryManager`
- **Key Responsibilities:**
  - Periodic retry of queued experiences/feedback (every 3 minutes)
  - Process queues when connectivity restored
  - Remove successfully sent items
- **Android Adaptation:**
  - Use Kotlin Coroutines `flow` with `onEach` and delay
  - Or use `Timer` with coroutine scope
  - Background processing with `Dispatchers.IO`

**1.7 Analytics Interface** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.analytics/`
- **Component:**
  - `IApperoAnalytics` - Interface for analytics delegation
- **Key Responsibilities:**
  - `logApperoFeedback(rating, feedback)` callback
  - `logRatingSelected(rating)` callback
- **Android Adaptation:**
  - Use Kotlin interface (equivalent to iOS protocol)
  - Optional nullable property on Appero instance

**1.8 Utilities & Debug** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.util/`
- **Components:**
  - `ApperoLogger` - Debug logging utility
- **Key Responsibilities:**
  - Conditional logging based on debug flag
  - Console output with `[Appero]` prefix
- **Android Adaptation:**
  - Use Android `Log` class instead of print
  - Conditional compilation not typically needed (runtime flag)

### 2. UI Module (Jetpack Compose) ✅ COMPLETE

**2.1 Main Feedback Composable** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.ui.ApperoFeedbackBottomSheet` + `ApperoFeedbackUI`
- **Purpose:** Main feedback UI dialog/sheet
- **Key Responsibilities:**
  - Modal presentation (Dialog or BottomSheet)
  - Dynamic flow based on FlowType
  - State management for rating selection
  - Feedback text input
  - Thanks screen display
- **Android Adaptation:**
  - Use Jetpack Compose `Dialog` or `ModalBottomSheet`
  - Use `rememberSaveable` for state
  - Support Material 3 design system
  - Handle keyboard and screen state

**2.2 Sub-Composables** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.ui.components/` + `ui.screens/`
- **Components:**
  - `RatingButton` + `RatingSelector` - 5-point rating selector (emoji icons) ✅
  - `FeedbackTextField` - Text field for feedback input ✅
  - `RatingSelectionScreen` - Initial rating selection screen ✅
  - `FeedbackInputScreen` - Feedback input flow (positive/negative) ✅
  - `ThankYouScreen` - Post-submission thank you screen ✅
- **Android Adaptation:**
  - Use Material 3 components (IconButton, TextField, Button)
  - Rating images from resources (drawable)
  - Text validation and character limits
  - Accessibility support

**2.3 XML Integration (Wrapper Views)** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.ui.xml/`
- **Component:**
  - `ApperoFeedbackComposeView` - ComposeView wrapper for XML layouts ✅
- **Purpose:** Allow Compose UI in XML-based layouts
- **Android Adaptation:**
  - Use `ComposeView` in custom View class
  - Expose necessary methods/properties for XML
  - Handle lifecycle appropriately

**2.4 Theme System** ✅
- **Location:** `uk.co.pocketworks.appero.sdk.main.ui.theme/`
- **Components:**
  - `ApperoTheme` - Interface defining theme properties ✅
  - `ApperoColors` - WCAG 2.2 AA compliant color palette ✅
  - `ApperoTypography` - Scalable typography (supports 200% text scaling) ✅
  - `ApperoShapes` - Shape definitions ✅
  - `DefaultApperoTheme` - Material 3 responsive theme ✅
  - `LightApperoTheme` - Fixed light theme ✅
  - `DarkApperoTheme` - Fixed dark theme ✅
  - `ApperoThemeProvider` - Composition local provider ✅
- **Key Responsibilities:**
  - Color definitions (background, text, buttons)
  - Typography definitions
  - Rating image resources
  - Material vs solid background option
- **Android Adaptation:**
  - Use Kotlin interface (like iOS protocol)
  - Use Android `Color` class
  - Use Material `Typography` or custom `Font` classes
  - Resource references for rating images
  - `@Composable` theme application

### 3. Resources ✅ COMPLETE

**3.1 Rating Images** ✅
- **Location:** `main/res/drawable/`
- **Files:**
  - `appero_rating_1.xml` - Red angry face (😡) ✅
  - `appero_rating_2.xml` - Orange frowning face (🙁) ✅
  - `appero_rating_3.xml` - Yellow neutral face (😐) ✅
  - `appero_rating_4.xml` - Light green smiling face (🙂) ✅
  - `appero_rating_5.xml` - Bright green very happy face (😄) ✅
- **Format:** Vector drawables (SVG converted) or PNG
- **Android Adaptation:**
  - Convert iOS rating images to Android drawable resources
  - Support vector drawables for scalability

**3.2 Localization Strings** ✅
- **Location:** `main/res/values/strings.xml` (and locale variants)
- **Strings Needed:**
  - DefaultTitle, DefaultSubtitle, DefaultPrompt
  - SendFeedback, Done, NotNow, Rate
  - Rating accessibility labels
  - Rating prompt messages (positive/neutral/negative)
  - Thank you messages
- **Android Adaptation:**
  - Use Android string resources
  - Support localization with `values-{locale}/`
  - String arrays if needed

### 4. Sample App Module ⏸️ PENDING

**4.1 Compose Sample** ⏸️
- **Location:** `sample/src/main/java/`
- **Purpose:** Demonstrate Jetpack Compose integration
- **Key Features:**
  - Initialization in Application class
  - Theme switching UI
  - Manual experience logging buttons
  - Automatic feedback prompt observation

**4.2 XML/View Sample (Optional)** ⏸️
- **Location:** `sample/src/main/java/`
- **Purpose:** Demonstrate XML layout integration
- **Key Features:**
  - ComposeView usage in XML
  - Manual trigger examples

### 5. Testing Module ⏸️ PENDING

**5.1 Unit Tests** ⏸️
- **Location:** `main/src/test/`
- **Test Targets:**
  - Appero singleton logic
  - Data models serialization
  - API client mocking
  - Storage operations
  - Queue processing logic

**5.2 UI Tests** ⏸️
- **Location:** `main/src/androidTest/`
- **Test Targets:**
  - Compose UI interactions
  - Dialog presentation
  - Theme application
  - User flows

### 6. Build Configuration

**6.1 Dependencies (libs.versions.toml)** ✅
- Kotlin Coroutines
- Jetpack Compose (UI, Material3)
- Kotlinx Serialization (or Gson/Moshi)
- Retrofit (or Ktor Client)
- AndroidX Core
- Material Components

**6.2 ProGuard Rules** ⏸️
- Keep data models for JSON serialization
- Keep public API classes
- Obfuscate internal implementation

## Key Architecture Decisions

1. **Reactive State:** Use Kotlin `StateFlow` instead of iOS `@Published` for observable state
2. **Coroutines:** All async operations use Kotlin Coroutines (`suspend` functions)
3. **Compose First:** UI built with Jetpack Compose, with XML wrapper support
4. **Material 3:** Use Material Design 3 components and theming
5. **Internal Storage:** JSON file stored in app's internal files directory (not Documents)
6. **SharedPreferences:** User ID stored in SharedPreferences (equivalent to UserDefaults)
7. **Network Monitoring:** Android ConnectivityManager with Flow-based reactive API
8. **Error Handling:** Kotlin `Result<T>` or sealed classes for type-safe error handling

## Module Structure

```
main/
├── src/main/
│   ├── java/uk/co/pocketworks/appero/sdk/main/
│   │   ├── Appero.kt                    # Main singleton
│   │   ├── api/
│   │   │   ├── ApperoAPIClient.kt
│   │   │   └── ApperoAPIError.kt
│   │   ├── model/
│   │   │   ├── ExperienceRating.kt
│   │   │   ├── Experience.kt
│   │   │   ├── QueuedFeedback.kt
│   │   │   ├── ApperoData.kt
│   │   │   ├── FeedbackUIStrings.kt
│   │   │   └── FlowType.kt
│   │   ├── storage/
│   │   │   ├── ApperoDataStorage.kt
│   │   │   └── UserPreferences.kt
│   │   ├── network/
│   │   │   └── NetworkMonitor.kt
│   │   ├── analytics/
│   │   │   └── IApperoAnalytics.kt
│   │   ├── ui/
│   │   │   ├── ApperoFeedbackDialog.kt
│   │   │   ├── components/
│   │   │   │   ├── FeedbackRatingView.kt
│   │   │   │   ├── FeedbackInputView.kt
│   │   │   │   ├── PositiveFlowView.kt
│   │   │   │   ├── NegativeFlowView.kt
│   │   │   │   └── ThanksView.kt
│   │   │   ├── xml/
│   │   │   │   └── ApperoFeedbackView.kt
│   │   │   └── theme/
│   │   │       ├── ApperoTheme.kt
│   │   │       ├── DefaultTheme.kt
│   │   │       ├── LightTheme.kt
│   │   │       └── DarkTheme.kt
│   │   └── util/
│   │       └── ApperoDebug.kt
│   └── res/
│       ├── drawable/        # Rating icons
│       ├── values/
│       │   └── strings.xml  # Localized strings
│       └── ...
```

## Implementation Phases (High-Level)

1. **Phase 1: Core Infrastructure** ✅ COMPLETE
   - Data models and serialization
   - Storage layer (JSON file + SharedPreferences)
   - Basic Appero singleton structure

2. **Phase 2: Networking** ✅ COMPLETE
   - API client implementation
   - Network monitoring
   - Error handling

3. **Phase 3: Offline Support** ✅ COMPLETE
   - Queue management
   - Retry mechanism
   - Experience/feedback queueing logic

4. **Phase 4: UI Foundation** ✅ COMPLETE
   - Theme system
   - Basic Compose components
   - Rating view

5. **Phase 5: Feedback UI** ✅ COMPLETE
   - Main feedback dialog
   - Flow-specific views
   - Thanks screen
   - XML wrapper

6. **Phase 6: Integration** ⏸️ IN PROGRESS
   - Analytics interface ✅
   - Sample apps ⏸️
   - Documentation ⏸️

7. **Phase 7: Polish** ⏸️ PENDING
   - Localization ⏸️
   - Testing ⏸️
   - ProGuard rules ⏸️
   - Performance optimization ⏸️

