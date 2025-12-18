# Appero SDK - Integration Guide

Complete guide for integrating the Appero SDK into your Android application.

## Prerequisites

- **Minimum SDK Version:** 24 (Android 7.0)
- **Compile SDK Version:** 36
- **Kotlin Version:** 2.0.21 or higher
- **Jetpack Compose:** Compose BOM 2024.09.03 or higher

## Installation

### Option 1: Local Module (Current)

1. Clone or download the Appero SDK repository
2. Add the `:main` module to your project's `settings.gradle.kts`:

```kotlin
include(":main")
project(":main").projectDir = file("path/to/ApperoSDKAndroid/main")
```

3. Add the dependency in your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":main"))
}
```

### Option 2: Maven/JitPack (Future)

```kotlin
dependencies {
    implementation("uk.co.pocketworks:appero-sdk:1.0.0-alpha")
}
```

## Quick Start

### 1. Installation

Add the Appero SDK module to your project:

```kotlin
// In your app's build.gradle.kts
dependencies {
    implementation(project(":main"))
    // Or when published: implementation("uk.co.pocketworks:appero-sdk:1.0.0-alpha")
}
```

### 2. Initialize

Initialize Appero in your `Application` class:

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Appero.instance.start(
            context = this,
            apiKey = "your_api_key",
            userId = "user_123", // Optional
            debug = BuildConfig.DEBUG
        )
    }
}
```

Don't forget to register in `AndroidManifest.xml`:

```xml

<application android:name=".MyApplication"/>
```

### 3. Display Feedback UI

Observe the feedback prompt state and display the UI:

```kotlin
@Composable
fun MyScreen() {
    val shouldShowFeedback by Appero.instance.shouldShowFeedbackPrompt.collectAsState()

    Box {
        MyAppContent()

        if (shouldShowFeedback) {
            ApperoFeedbackUI()
        }
    }
}
```

### 4. Log Experiences

Track user experiences throughout your app:

```kotlin
// Log positive experience
Appero.instance.log(ExperienceRating.POSITIVE)

// Log with context
Appero.instance.log(
    rating = ExperienceRating.NEGATIVE,
    detail = "Checkout flow failed"
)
```

That's it! The SDK handles everything else automatically.

## Core Concepts

### Experience Logging

The SDK tracks user experiences and automatically determines when to show a feedback prompt based on:
- **3+ positive ratings** → Prompts for positive feedback
- **1 negative rating** → Immediately prompts for negative feedback
- **Offline queueing** → Experiences are queued when offline and sent when online

### Feedback Flows

Three distinct flows based on user sentiment:
- **Positive Flow:** Thank you message, optional text feedback
- **Neutral Flow:** Asks what could be improved
- **Negative Flow:** Apologizes, asks what went wrong

### Offline Support

The SDK handles offline scenarios gracefully:
- Experiences logged while offline are queued locally
- Automatic retry every 3 minutes when online
- Network state monitoring with reconnection handling
- JSON file storage in app's internal storage

### Rating Options
- `ExperienceRating.STRONG_POSITIVE` (5) - 😄 Very satisfied
- `ExperienceRating.POSITIVE` (4) - 🙂 Satisfied
- `ExperienceRating.NEUTRAL` (3) - 😐 Neutral
- `ExperienceRating.NEGATIVE` (2) - 🙁 Dissatisfied
- `ExperienceRating.STRONG_NEGATIVE` (1) - 😡 Very dissatisfied

## Analytics Integration

Track Appero events in your analytics platform:

```kotlin
Appero.instance.analyticsDelegate = object : IApperoAnalytics {
    override fun logApperoFeedback(rating: Int, feedback: String) {
        // Called when user submits feedback
        // rating: 1-5
        // feedback: User's text input (may be empty)

        // Example: Firebase Analytics
        Firebase.analytics.logEvent("appero_feedback") {
            param("rating", rating.toLong())
            param("feedback", feedback)
        }
    }

    override fun logRatingSelected(rating: Int) {
        // Called when user selects a rating (before submitting feedback)
        // rating: 1-5

        Firebase.analytics.logEvent("appero_rating_selected") {
            param("rating", rating.toLong())
        }
    }
}
```

## Theme Customization

Appero provides three built-in themes and supports custom branding.

### Built-in Themes

**1. System Theme (Default)**

- Adapts to device light/dark mode automatically
- Uses Material 3 design system
- Best for apps following Material Design

**2. Light Theme**

- Fixed light color palette
- Consistent across all devices
- Good for apps with light branding

**3. Dark Theme**

- Fixed dark color palette
- Optimized for dark mode apps

### Using Themes

```kotlin
// System theme (default)
ApperoFeedbackUI()

// Fixed light theme
ApperoFeedbackUI(customTheme = LightApperoTheme)

// Fixed dark theme
ApperoFeedbackUI(customTheme = DarkApperoTheme)

// Dynamic based on app theme
val theme = if (isSystemInDarkTheme()) DarkApperoTheme else LightApperoTheme
ApperoFeedbackUI(customTheme = theme)
```

### Custom Brand Theme

Create a theme matching your brand colors:

```kotlin
object MyBrandTheme : ApperoTheme {
    override val colors = ApperoLightColors(
        primary = Color(0xFF6200EE),      // Your brand color
        onPrimary = Color.White,
        background = Color.White,
        surface = Color.White,
        onSurface = Color(0xFF1C1B1F),
        onSurfaceVariant = Color(0xFF49454F),
        // Rating colors (or use defaults)
        rating1 = Color(0xFFD64545),
        rating2 = Color(0xFFE87E3C),
        rating3 = Color(0xFFC99A1F),
        rating4 = Color(0xFF4A9E4E),
        rating5 = Color(0xFF3D8B41)
    )
    override val typography = ApperoTypography()
    override val shapes = ApperoShapes()
}

// Use your custom theme
ApperoFeedbackUI(customTheme = MyBrandTheme)
```

**Note:** All colors must meet WCAG 2.2 AA contrast requirements (4.5:1 for text, 3:1 for UI components).

## Advanced Usage

### Updating User ID

Set or update the user ID at any time:

```kotlin
Appero.instance.userId = "new_user_id"
```

## Permissions

Add these permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## ProGuard / R8

If using ProGuard or R8, add these rules to `proguard-rules.pro`:

```proguard
# Appero SDK
-keep class uk.co.pocketworks.appero.sdk.main.** { *; }
-keepclassmembers class uk.co.pocketworks.appero.sdk.main.model.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Ktor
-keep class io.ktor.** { *; }
```

## Troubleshooting

See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for common issues and solutions.

## Sample App

Run the `:sample` module to see a complete integration example matching the iOS reference design.

## Support

For issues or questions:
- GitHub Issues: [github.com/pocketworks/appero-sdk-android/issues](https://github.com/pocketworks/appero-sdk-android/issues)
- Documentation: [README.md](README.md)

## License

MIT License - See LICENSE file for details.
