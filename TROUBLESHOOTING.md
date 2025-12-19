# Appero SDK - Troubleshooting Guide

Solutions to common issues when integrating the Appero SDK.

## Table of Contents

- [Integration Issues](#integration-issues)
- [Feedback UI Issues](#feedback-ui-issues)
- [Network Issues](#network-issues)
- [Theme Issues](#theme-issues)
- [Build Issues](#build-issues)
- [Testing Issues](#testing-issues)

---

## Integration Issues

### Feedback UI Not Showing

**Problem:** `shouldShowFeedbackPrompt` never becomes true.

**Possible Causes & Solutions:**

1. **SDK not initialized**
   ```kotlin
   // ❌ Forgot to initialize
   class MyApplication : Application()

   // ✅ Correct
   class MyApplication : Application() {
       override fun onCreate() {
           super.onCreate()
           Appero.instance.start(this, "YOUR_API_KEY")
       }
   }
   ```

2. **Not observing StateFlow**
   ```kotlin
   // ❌ Not collecting state
   if (Appero.instance.shouldShowFeedbackPrompt.value) { ... }

   // ✅ Correct
   val shouldShow by Appero.instance.shouldShowFeedbackPrompt.collectAsState()
   if (shouldShow) { ... }
   ```

3. **Rating threshold not met**
   - review the "Experience Threshold" setting in the Appero dashboard

4. **Application class not registered**
   ```xml
   <!-- ❌ Missing android:name -->
   <application>

   <!-- ✅ Correct -->
   <application android:name=".MyApplication">
   ```

---

### User ID Not Set

**Problem:** Experiences logged but userId is null.

**Solution:** Set userId at initialization.

```kotlin
Appero.instance.start(
    context = this,
    apiKey = "API_KEY",
    userId = "user_123"
)
```

---

## Feedback UI Issues

### UI Not Styled Correctly

**Problem:** Colors or fonts don't match your app.

**Solution:** Use custom theme.

```kotlin
// Create custom theme
object MyTheme : ApperoTheme {
    override val colors = ApperoLightColors(
        primary = Color(0xFF6200EE),
        // ... your brand colors
    )
    override val typography = DefaultApperoTypography()
    override val shapes = ApperoShapes()
    override val ratingImages = DefaultApperoRatingImages()
}

// Apply custom theme
ApperoFeedbackBottomSheet(customTheme = MyTheme)
```

---

### Bottom Sheet Not Dismissing

**Problem:** Can't close the feedback UI.

**Solution:** Ensure you're using `ApperoFeedbackBottomSheet` which handles dismissal automatically.

```kotlin
// ✅ Handles dismissal internally
ApperoFeedbackBottomSheet()


// If using custom implementation, observe dismissal:
LaunchedEffect(shouldShow) {
    if (!shouldShow) {
        // UI was dismissed
        hideCustomFeedbackUI()
    }
}
```

---

### Text Input Not Working

**Problem:** Can't type in feedback text field.

**Solution:** This is handled internally by the SDK. If you're implementing custom UI, ensure proper state management:

```kotlin
var text by remember { mutableStateOf("") }

OutlinedTextField(
    value = text,
    onValueChange = { if (it.length <= 240) text = it },
    maxLines = 5
)
```

---

## Network Issues

### API Key Invalid

**Problem:** Logs show 401 Unauthorized errors.

**Solution:** Verify your API key is correct.

```kotlin
// Enable debug logging to see errors
Appero.instance.start(
    context = this,
    apiKey = "YOUR_CORRECT_API_KEY",
    debug = true // Shows API errors in logcat
)

// Check logcat for errors
// Tag: [Appero]
```

---

### Experiences Not Sending

**Problem:** Experiences logged but never reach the server.

**Possible Causes & Solutions:**

1. **Invalid API Key**
   - make sure you have set the correct API key in 
   ```kotlin
   Appero.instance.start(context, apiKey)
   ```

2. **Device is offline**
   - Experiences are queued automatically
   - Will retry every 3 minutes when online
   - Check with:
   ```kotlin
   // Enable debug mode to see retry attempts
   Appero.instance.start(context, apiKey, debug = true)
   // Watch logcat for "[Appero] Retrying queued experiences"
   ```

3. **Firewall or proxy blocking requests**
   - Endpoint: `https://app.appero.co.uk/api/v1`
   - Ensure this domain is whitelisted

---

### Offline Queueing Not Working

**Problem:** Experiences lost when offline.

**Solution:** SDK handles this automatically. Verify with debug logs:

```kotlin
// Turn on debug logging
Appero.instance.start(context, apiKey, debug = true)

// Log experience while offline
Appero.instance.log(ExperienceRating.POSITIVE)
// Check logcat: "[Appero] Network offline - queuing experience"

// When network returns
// Check logcat: "[Appero] Processing 1 unsent experiences"
```

---

## Theme Issues

### Colors Don't Meet WCAG Standards

**Problem:** Accessibility scanner flags contrast issues.

**Solution:** Use built-in themes or verify contrast ratios.

```kotlin
// Built-in themes are WCAG 2.2 AA compliant
ApperoFeedbackBottomSheet(customTheme = LightApperoTheme)

// For custom themes, verify contrast at:
// https://webaim.org/resources/contrastchecker/

// Minimum contrast ratios:
// - Text on background: 4.5:1
// - Large text (18pt+): 3:1
// - UI components: 3:1
```

---

### Dark Theme Not Working

**Problem:** Dark theme shows light colors.

**Solution:** Explicitly use DarkApperoTheme.

```kotlin
@Composable
fun MyScreen() {
    val isDark = isSystemInDarkTheme()
    ApperoFeedbackBottomSheet(customTheme = if (isDark) DarkApperoTheme else LightApperoTheme)
}
```

---

### Custom Theme Not Applied

**Problem:** Custom theme properties not showing.

**Solution:** Ensure all properties are implemented.

```kotlin
// ❌ Incomplete theme
object MyTheme : ApperoTheme {
    override val colors = ApperoLightColors(
        primary = Color.Red
        // Missing other colors - they'll use defaults
    )
}

// ✅ Complete theme
object MyTheme : ApperoTheme {
    override val colors = ApperoLightColors(
        primary = Color.Red,
        onPrimary = Color.White,
        background = Color.White,
        surface = Color.White,
        onSurface = Color.Black,
    )
    override val typography = DefaultApperoTypography()
    override val shapes = ApperoShapes()
    override val ratingImages = DefaultApperoRatingImages()
}
```

---

## Build Issues

### Unresolved Reference: Appero

**Problem:** Import fails with "Unresolved reference: Appero".

**Solution:** Ensure module dependency is added.

```kotlin
// In app's build.gradle.kts
dependencies {
    implementation(project(":main")) // If local module
    // OR
    implementation("uk.co.pocketworks.appero:sdk-android:<latest-version>") // If from Maven
}
```

Then sync project with Gradle files.

---

### Compose Version Conflicts

**Problem:** Build fails with Compose version mismatch.

**Solution:** Use same Compose BOM version as SDK.

```kotlin
// In your app's build.gradle.kts
dependencies {
    // Use same BOM version
    implementation(platform("androidx.compose:compose-bom:2024.09.03"))

    // Then add Compose dependencies
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
}
```

---

### Kotlin Version Mismatch

**Problem:** "Kotlin version mismatch" error.

**Solution:** Use Kotlin 2.0.21 or higher.

```kotlin
// In project build.gradle.kts or libs.versions.toml
kotlin = "2.0.21"
```

---

### ProGuard Stripping SDK Code

**Problem:** SDK crashes in release build.

**Solution:** Add ProGuard rules.

```proguard
# Add to proguard-rules.pro

# Appero SDK
-keep class uk.co.pocketworks.appero.sdk.main.** { *; }
-keepclassmembers class uk.co.pocketworks.appero.sdk.main.model.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
```

---

## Testing Issues

### Can't Trigger Feedback in Tests

**Problem:** Need to trigger feedback prompt programmatically.

**Solution:** Log multiple experiences.

```kotlin
@Test
fun testFeedbackPrompt() {
    // Trigger positive flow
    repeat(3) {
        Appero.instance.log(ExperienceRating.POSITIVE)
    }

    // Verify prompt appears
    assertTrue(Appero.instance.shouldShowFeedbackPrompt.value)
}

@Test
fun testNegativeFlow() {
    // Trigger negative flow (only needs 1)
    Appero.instance.log(ExperienceRating.STRONG_NEGATIVE)

    // Verify prompt appears
    assertTrue(Appero.instance.shouldShowFeedbackPrompt.value)
}
```

---

### State Persists Between Tests

**Problem:** Previous test affects next test.

**Solution:** Reset SDK before each test.

```kotlin
@Before
fun setup() {
    Appero.instance.reset()
    Appero.instance.start(context, "test_api_key", debug = true)
}

@After
fun tearDown() {
    Appero.instance.reset()
}
```

---

### Can't Mock Network Responses

**Problem:** Need to test offline behavior.

**Solution:** SDK handles offline automatically. To test:

```kotlin
@Test
fun testOfflineQueueing() {
    // 1. Disable network (using test environment or mock)
    setNetworkEnabled(false)

    // 2. Log experience
    Appero.instance.log(ExperienceRating.POSITIVE)

    // 3. Verify queued (check debug logs or internal state)
    // SDK will queue it automatically

    // 4. Re-enable network
    setNetworkEnabled(true)

    // 5. Wait for retry (automatic every 3 minutes)
    // Or trigger manually if you have access to RetryManager
}
```

---

## Debug Tips

### Enable Debug Logging

```kotlin
Appero.instance.start(
    context = this,
    apiKey = "API_KEY",
    debug = true
)
```

**Logcat filter:** `[Appero]`

**What you'll see:**
- API requests and responses
- Network status changes
- Queue processing
- Experience logging
- Errors and warnings

---

## Getting Help

If your issue isn't covered here:

1. **Check Documentation:**
   - [Integration Guide](INTEGRATION.md)

2. **Enable Debug Logging:**
   ```kotlin
   Appero.instance.start(context, apiKey, debug = true)
   ```

3. **Check GitHub Issues:**
   - Search existing issues
   - Create new issue with:
     - SDK version
     - Android version
     - Stack trace
     - Debug logs

4. **Sample App:**
   - Run the `:sample-compose` module
   - Compare with your implementation

---

## Common Error Messages

### "API key not set"
**Cause:** Forgot to call `start()`
**Fix:** Initialize in `Application.onCreate()`

### "Network error 401"
**Cause:** Invalid API key
**Fix:** Verify API key is correct

### "Network error 422"
**Cause:** Invalid data format
**Fix:** Update to latest SDK version

### "StateFlow not initialized"
**Cause:** Accessing SDK before `start()` called
**Fix:** Call `start()` first in `Application.onCreate()`

---

## See Also

- [Integration Guide](INTEGRATION.md) - Setup instructions
