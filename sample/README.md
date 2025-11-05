# Appero SDK Sample App

Demonstration app showcasing the Appero SDK integration in a real Android application.

## Overview

This sample app provides a complete working example of the Appero SDK, matching the iOS reference design. It demonstrates all key features including SDK initialization, experience logging, theme switching, and feedback UI display.

## Features

### 1. Theme Switching
Toggle between three theme modes:
- **System** - Material 3 theme (adapts to device light/dark mode)
- **Light** - Fixed light theme
- **Dark** - Fixed dark theme

The selected theme applies to the Appero feedback UI when it appears.

### 2. Experience Logging
Five buttons to log different rating levels:
- **Very Positive** (😄) - Rating 5, green background
- **Positive** (🙂) - Rating 4, light green background
- **Neutral** (😐) - Rating 3, beige background
- **Negative** (🙁) - Rating 2, light pink background
- **Very Negative** (😡) - Rating 1, pink background

Each button logs an experience with the corresponding rating.

### 3. Automatic Feedback Prompt
The app observes `shouldShowFeedbackPrompt` and automatically displays the feedback UI when:
- 3+ positive experiences are logged (ratings 4-5)
- 1 negative experience is logged (ratings 1-2)

### 4. Manual Trigger
"Manually Trigger Feedback" button logs 3 positive experiences at once to immediately show the feedback prompt for testing.

## Running the Sample

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24 or higher
- Kotlin 2.0.21 or higher

### Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/pocketworks/appero-sdk-android.git
   cd appero-sdk-android
   ```

2. **Open in Android Studio:**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync

3. **Run the sample app:**
   - Select `:sample` run configuration
   - Choose a device or emulator
   - Click Run (▶️)

### API Key

The sample uses a demo API key: `"demo_api_key_12345"`

For production apps, replace with your actual Appero API key:
```kotlin
// In SampleApplication.kt
Appero.instance.start(
    context = this,
    apiKey = "your_actual_api_key",
    userId = "demo_user_123",
    debug = true
)
```

## Project Structure

```
sample/
├── src/main/
│   ├── java/uk/co/pocketworks/appero/sample/
│   │   ├── SampleApplication.kt          # SDK initialization
│   │   ├── MainActivity.kt                # Main screen
│   │   └── components/
│   │       ├── RatingDemoButton.kt       # Rating button component
│   │       └── ThemeSelector.kt          # Theme switcher component
│   ├── res/
│   │   ├── values/
│   │   │   └── strings.xml               # App strings
│   │   └── ...
│   └── AndroidManifest.xml                # App configuration
└── build.gradle.kts                       # Module dependencies
```

## Key Files

### SampleApplication.kt
Initializes the Appero SDK in `Application.onCreate()`:
```kotlin
Appero.instance.start(
    context = this,
    apiKey = "demo_api_key_12345",
    userId = "demo_user_123",
    debug = true
)
```

### MainActivity.kt
Main screen with:
- Theme selector
- 5 rating buttons
- Manual trigger button
- Feedback UI display logic

### Components
- **RatingDemoButton** - Colored button matching iOS design
- **ThemeSelector** - iOS-style segmented control for theme selection

## What to Explore

### 1. Basic Flow
1. Tap any rating button 3 times
2. Feedback prompt appears automatically
3. Select a rating and provide feedback
4. Submit and see thank you message

### 2. Theme Switching
1. Switch between System/Light/Dark themes
2. Tap a rating button to trigger feedback
3. Notice how the feedback UI adopts the selected theme

### 3. Negative Flow
1. Tap "Very Negative" button once
2. Feedback prompt appears immediately
3. Different question appears: "We're sorry you're not enjoying it..."

### 4. Manual Trigger
1. Tap "Manually Trigger Feedback" button
2. Logs 3 positive experiences at once
3. Feedback prompt appears immediately

### 5. Offline Behavior
1. Turn off device network/Wi-Fi
2. Tap rating buttons
3. Experiences are queued locally
4. Turn network back on
5. Queued experiences send automatically (every 3 minutes)

## Design

The sample app matches the iOS reference design:
- **Title:** "Appero SDK Demo"
- **Theme Selector:** Segmented control style
- **Rating Buttons:** Full-width with icons and colors
- **Manual Trigger:** Bottom button for testing
- **Layout:** Single-screen, scrollable design

### Colors
```kotlin
Very Positive:  #D4F4DD  (light green)
Positive:       #E8F5E9  (lighter green)
Neutral:        #FFF3E0  (beige)
Negative:       #FFEBEE  (light pink)
Very Negative:  #FFCDD2  (pink)
```

## Testing

### Test Positive Flow
```kotlin
repeat(3) {
    Appero.instance.log(ExperienceRating.POSITIVE)
}
// Feedback UI should appear
```

### Test Negative Flow
```kotlin
Appero.instance.log(ExperienceRating.STRONG_NEGATIVE)
// Feedback UI should appear immediately
```

### Test Theme Switching
```kotlin
// Switch theme
val theme = when (selectedTheme) {
    ThemeMode.LIGHT -> LightApperoTheme
    ThemeMode.DARK -> DarkApperoTheme
    ThemeMode.SYSTEM -> null
}
ApperoFeedbackUI(customTheme = theme)
```

## Customization

### Change API Key
Edit `SampleApplication.kt`:
```kotlin
apiKey = "your_api_key"
```

### Change User ID
Edit `SampleApplication.kt`:
```kotlin
userId = "your_user_id"
```

### Add Custom Theme
1. Create theme object:
   ```kotlin
   object MyTheme : ApperoTheme {
       override val colors = ApperoLightColors(...)
       override val typography = ApperoTypography()
       override val shapes = ApperoShapes()
   }
   ```

2. Add to theme selector in `MainActivity.kt`:
   ```kotlin
   val customTheme = when (selectedTheme) {
       ThemeMode.CUSTOM -> MyTheme
       // ... other themes
   }
   ```

### Add Analytics
Edit `SampleApplication.kt`:
```kotlin
Appero.instance.analyticsDelegate = object : IApperoAnalytics {
    override fun logApperoFeedback(rating: Int, feedback: String) {
        // Log to your analytics platform
    }

    override fun logRatingSelected(rating: Int) {
        // Track rating selection
    }
}
```

## Troubleshooting

### Sample won't build
1. Sync Gradle files
2. Clean and rebuild: `Build > Clean Project`
3. Invalidate caches: `File > Invalidate Caches / Restart`

### Feedback UI not showing
1. Check logcat for `[Appero]` tag
2. Enable debug mode: `debug = true` in `SampleApplication.kt`
3. Verify 3+ positive ratings logged

### Theme not applying
1. Ensure theme object is not null
2. Check theme is passed to `ApperoFeedbackUI(customTheme = ...)`

## Learn More

- **Integration Guide:** [../INTEGRATION.md](../INTEGRATION.md)
- **API Reference:** [../API.md](../API.md)
- **Code Examples:** [../EXAMPLES.md](../EXAMPLES.md)
- **Troubleshooting:** [../TROUBLESHOOTING.md](../TROUBLESHOOTING.md)

## SDK Documentation

For complete SDK documentation, see:
- Main README: [../README.md](../README.md)
- Integration steps
- API reference
- Theme customization
- Analytics integration

## Support

For issues or questions:
- GitHub Issues: [github.com/pocketworks/appero-sdk-android/issues](https://github.com/pocketworks/appero-sdk-android/issues)
- Sample-specific issues: Tag with `sample-app`

## License

MIT License - See [../LICENSE](../LICENSE) for details.
