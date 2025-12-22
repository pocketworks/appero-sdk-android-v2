# Appero SDK - Integration Guide

Complete guide for integrating the Appero SDK into your Android application.

## Prerequisites

- **Minimum SDK Version:** 24 (Android 7.0)
- **Compile SDK Version:** 36
- **Kotlin Version:** 2.0.21 or higher
- **Jetpack Compose:** Compose BOM 2024.09.03 or higher

## Installation

### Option 1: Local Module

1. Clone or download the Appero SDK repository
2. Add the `:main` module to your project's `settings.gradle.kts`:

```kotlin
include(":main")
project(":main").projectDir = file("path/to/ApperoSDKAndroid/main")
```

3. Add the dependency in your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":main"))
}
```

### Option 2: Maven Central

```kotlin
dependencies {
    implementation("uk.co.pocketworks.appero:sdk-android:<latest-version>")
}
```

## Quick Start

### 1. Installation

Add the Appero SDK module to your project:

```kotlin
// In your module's build.gradle.kts
dependencies {
    implementation("uk.co.pocketworks.appero:sdk-android:<latest-version>")
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

Don't forget to register your custom Application in `AndroidManifest.xml`:

```xml

<application android:name=".MyApplication"/>
```

### 3. Add the Appero Feedback UI to your screen layout (Jetpack Compose app example)

Add this composable to your app's composition to enable automatic feedback collection.
The modal will appear automatically when the Appero SDK determines it's appropriate based on user experiences.

```kotlin
@Composable
fun MyScreen() {
    Box {
        MyAppContent()
        ApperoFeedbackBottomSheet()
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
ApperoFeedbackBottomSheet()

// Fixed light theme
ApperoFeedbackBottomSheet(customTheme = LightApperoTheme)

// Fixed dark theme
ApperoFeedbackBottomSheet(customTheme = DarkApperoTheme)
```

### Custom Brand Theme

Create a theme matching your brand colors:

```kotlin
object MyBrandTheme : ApperoTheme {
    override val colors = ApperoLightColors(
        primary = Color(0xFF6200EE),      // Your brand color
        onPrimary = Color.White,
        surface = Color.White,
        onSurface = Color(0xFF1C1B1F),
        onSurfaceVariant = Color(0xFF49454F),
    )
    override val typography = DefaultApperoTypography()
    override val shapes = ApperoShapes()
    override val ratingImages = DefaultApperoRatingImages()
}

// Use your custom theme
ApperoFeedbackBottomSheet(customTheme = MyBrandTheme)
```

## Advanced Usage

## Permissions

Appero declares these permissions in its `AndroidManifest.xml`:

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

Run the `:sample-compose` module to see a complete integration example in an app built with Jetpack Compose.
To see an integration example in a an app built with XML layouts, run the `:sample-xml` module.

Make sure to substitute your api-key in the `SampleApplication` class in order to be able to send events to the
Appero back-end.

## Contributing

Contributions are welcome!

## Support

For issues or questions:
- GitHub Issues: [GitHub Issues](https://github.com/pocketworks/appero-sdk-android-v2/issues)

## License

MIT License - See [LICENSE](LICENCE) file for details.
