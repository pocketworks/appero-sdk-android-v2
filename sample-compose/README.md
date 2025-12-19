# Appero SDK Sample App

Demonstration app showcasing the Appero SDK integration in a real Android application.

## Overview

This sample app provides a complete working example of the Appero SDK. It demonstrates all key features 
including SDK initialization, experience logging, theme switching, and feedback UI display.

## Features

### 1. Theme Switching
Toggle between three theme modes:
- **System** - Material 3 theme (adapts to device light/dark mode)
- **Custom 1** - Fixed custom theme
- **Cuztom 2** - Another fixed custom theme

The selected theme applies to the Appero feedback UI when it appears.

### 2. Experience Logging
Five buttons to log different rating levels:
- **Very Positive** (😄) - Rating 5
- **Positive** (🙂) - Rating 4
- **Neutral** (😐) - Rating 3
- **Negative** (🙁) - Rating 2
- **Very Negative** (😡) - Rating 1

Each button logs an experience with the corresponding rating.

### 3. Automatic Feedback Prompt
The app observes `shouldShowFeedbackPrompt` and automatically displays the feedback UI when it's instructed to do so
by the back-end.

### 4. Manual Trigger
"Manually Trigger Feedback" immediately shows the feedback prompt for testing.

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
   - Select `:sample-compose` run configuration
   - Choose a device or emulator
   - Click Run (▶️)

### API Key

Sign-in to the Appero Dashboard (https://app.appero.co.uk/) to get your API key.

```kotlin
// In SampleApplication.kt
Appero.instance.start(
    context = this,
    apiKey = "your_api_key",
    userId = "demo_user_123",
    debug = true
)
```

## Support

For issues or questions:
- GitHub Issues: [GitHub Issues](https://github.com/pocketworks/appero-sdk-android-v2/issues)
- Sample-specific issues: Tag with `sample-app`

## License

MIT License - See LICENSE file for details.
