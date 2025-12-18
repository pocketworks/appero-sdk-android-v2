# Appero SDK for Android

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

Run the included sample app to see Appero in action:

```bash
# Clone the repository
git clone https://github.com/pocketworks/appero-sdk-android.git

# Open in Android Studio
# Select :sample run configuration
# Run on device or emulator
```

See [sample/README.md](sample/README.md) for details.

## How It Works

### Offline Support

Appero caches experiences and feedback locally when the device is offline. The SDK automatically:

- Stores data in a JSON file in your app's internal storage
- Monitors network connectivity
- Retries sending queued data every 3 minutes when online
- Removes successfully sent items from the queue

### Data Storage

- **Experiences & Feedback:** JSON file in app's internal storage
- **User ID:** SharedPreferences for persistence
- **Privacy First:** No sensitive data sent to servers without your control

We recommend using a consistent user ID across your backend, Appero, and analytics services for easier data management.

### Getting Started

The Appero SDK uses a singleton pattern accessible from anywhere in your code once initialized. We recommend
initializing in your `Application` class's `onCreate()` method.

## Monitoring User Experience

Appero tracks positive and negative user experiences. Once the number of experiences crosses defined thresholds, the SDK
prompts users for feedback.

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

