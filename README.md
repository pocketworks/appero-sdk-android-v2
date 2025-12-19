# Module Appero SDK for Android

The in-app feedback widget that drives organic growth.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Appero helps you capture user feedback at the right moments in your app journey. Built natively for Android with Kotlin
and Jetpack Compose.

## Features

✅ **Automatic Feedback Prompts** - Smart triggers based on user experience
✅ **Offline Support** - Queues experiences when offline, syncs automatically
✅ **Native UI** - Built with Jetpack Compose, fully customizable themes
✅ **WCAG 2.2 AA Compliant** - Accessible by default with proper contrast ratios
✅ **Analytics Integration** - Easy integration with Firebase, GA4, and custom platforms
✅ **Zero Dependencies** - Self-contained with Kotlin Coroutines and Ktor

## Documentation

📚 **Complete Documentation:**

- **[Integration Guide](INTEGRATION.md)** - Detailed setup instructions
- **[Troubleshooting](TROUBLESHOOTING.md)** - Common issues and solutions

## Sample App

Run the included sample apps to see Appero in action:

```bash
# Clone the repository
git clone https://github.com/pocketworks/appero-sdk-android-v2.git

# Open in Android Studio
# Select :sample-compose or :sample-xml run configuration
# Run on device or emulator
```

See [sample/README.md](sample/README.md) for details.

## Core Concepts

### Experience Logging

The SDK tracks user experiences and automatically determines when to show a feedback prompt based on the
Experience Threshold set in the Appero Dashboard.

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
- JSON file storage in app's internal storage (shared preferences)

### Rating Options
- `ExperienceRating.STRONG_POSITIVE` (5) - 😄 Very satisfied
- `ExperienceRating.POSITIVE` (4) - 🙂 Satisfied
- `ExperienceRating.NEUTRAL` (3) - 😐 Neutral
- `ExperienceRating.NEGATIVE` (2) - 🙁 Dissatisfied
- `ExperienceRating.STRONG_NEGATIVE` (1) - 😡 Very dissatisfied

### Getting Started

The Appero SDK uses a singleton pattern accessible from anywhere in your code once initialized. We recommend
initializing in your `Application` class's `onCreate()` method.

### When to Log Experiences

Typical scenarios for logging experiences:

**Positive Experiences (😄 / 🙂):**

- Successful completion of user flows
- Feature usage that delights users
- Smooth transactions or purchases
- Positive responses to content

**Negative Experiences (😡 / 🙁):**

- Failed operations or error states
- Abandoned flows
- Server errors or timeouts
- User frustration indicators

**Neutral Experiences (😐):**

- Completed but suboptimal flows
- Feature discovery without engagement
- Canceled operations

### Example Usage

```kotlin
// After successful purchase
fun onPurchaseComplete() {
    Appero.instance.log(
        rating = ExperienceRating.POSITIVE,
        detail = "Purchase completed successfully"
    )
}

// After error
fun onCheckoutError(error: Exception) {
    Appero.instance.log(
        rating = ExperienceRating.NEGATIVE,
        detail = "Checkout failed: ${error.message}"
    )
}
```

**⚠️ Important:** Avoid logging sensitive information (addresses, phone numbers, emails, payment details) in the detail
field.

## Requirements

- **Minimum SDK:** 24 (Android 7.0 / Nougat)
- **Compile SDK:** 36
- **Kotlin:** 2.0.21 or higher
- **Compose BOM:** 2024.09.03 or higher

## Architecture

- **Language:** Kotlin with Coroutines
- **UI Framework:** Jetpack Compose with Material 3
- **Networking:** Ktor Client
- **Serialization:** kotlinx.serialization
- **State Management:** Kotlin StateFlow
- **Storage:** JSON file + SharedPreferences

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

MIT License - See [LICENSE](LICENSE) for details.

## Support

- **Documentation:** See links above
- **Issues:** [GitHub Issues](https://github.com/pocketworks/appero-sdk-android/issues)
- **Email:** support@appero.co.uk

## Credits

Developed by [Pocketworks Mobile](https://pocketworks.co.uk)

---

**Made with ❤️ for Android developers**

