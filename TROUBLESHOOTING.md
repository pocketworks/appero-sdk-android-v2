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
   - Need 3+ positive ratings (4-5) OR
   - 1 negative rating (1-2)

   ```kotlin
   // Force trigger for testing
   repeat(3) {
       Appero.instance.log(ExperienceRating.POSITIVE)
   }
   ```

4. **Application class not registered**
   ```xml
   <!-- ❌ Missing android:name -->
   <application>

   <!-- ✅ Correct -->
   <application android:name=".MyApplication">
   ```

---

### Feedback UI Disappears Immediately

**Problem:** Feedback UI flashes and disappears.

**Solution:** Don't dismiss prompt in the same frame it appears.

```kotlin
// ❌ Wrong
LaunchedEffect(shouldShow) {
    if (shouldShow) {
        Appero.instance.dismissApperoPrompt()
    }
}

// ✅ Correct - Let user dismiss naturally
if (shouldShow) {
    ApperoFeedbackUI() // Handles dismissal internally
}
```

---

### User ID Not Set

**Problem:** Experiences logged but userId is null.

**Solution:** Set userId before or after initialization.

```kotlin
// Option 1: During initialization
Appero.instance.start(
    context = this,
    apiKey = "API_KEY",
    userId = "user_123"
)

// Option 2: Set later
Appero.instance.userId = getCurrentUserId()
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
    override val typography = ApperoTypography()
    override val shapes = ApperoShapes()
}

// Apply custom theme
ApperoFeedbackUI(customTheme = MyTheme)
```

---

### Bottom Sheet Not Dismissing

**Problem:** Can't close the feedback UI.

**Solution:** Ensure you're using `ApperoFeedbackUI` which handles dismissal automatically.

```kotlin
// ✅ Handles dismissal internally
if (shouldShow) {
    ApperoFeedbackUI()
}

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

1. **No internet permission**
   ```xml
   <!-- Add to AndroidManifest.xml -->
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
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
ApperoFeedbackUI(customTheme = LightApperoTheme)

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
    val shouldShow by Appero.instance.shouldShowFeedbackPrompt.collectAsState()

    if (shouldShow) {
        val theme = if (isDark) DarkApperoTheme else LightApperoTheme
        ApperoFeedbackUI(customTheme = theme)
    }
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
        onSurfaceVariant = Color.Gray,
        rating1 = Color(0xFFD64545),
        rating2 = Color(0xFFE87E3C),
        rating3 = Color(0xFFC99A1F),
        rating4 = Color(0xFF4A9E4E),
        rating5 = Color(0xFF3D8B41)
    )
    override val typography = ApperoTypography()
    override val shapes = ApperoShapes()
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
    implementation("uk.co.pocketworks:appero-sdk:1.0.0") // If from Maven
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

## Performance Issues

### UI Lags When Showing Feedback

**Problem:** Janky animation when feedback UI appears.

**Solution:** Ensure you're using `rememberSaveable` for state.

```kotlin
@Composable
fun MyScreen() {
    val shouldShow by Appero.instance.shouldShowFeedbackPrompt.collectAsState()

    // Use Box to layer content
    Box {
        // Main content (always rendered)
        MyContent()

        // Feedback overlay (conditional)
        if (shouldShow) {
            ApperoFeedbackUI()
        }
    }
}
```

---

### High Memory Usage

**Problem:** App uses more memory after integrating SDK.

**Solution:** SDK is designed to be lightweight. Verify you're not creating multiple instances:

```kotlin
// ❌ Don't create copies
val appero1 = Appero.instance
val appero2 = Appero.instance // Same instance

// ✅ Use singleton
Appero.instance.log(...)
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

### Check Local Storage

SDK stores data in: `{app_internal_storage}/ApperoData.json`

```kotlin
// Access file location
val file = File(context.filesDir, "ApperoData.json")
val data = file.readText()
println("Stored data: $data")
```

---

### Verify StateFlow Updates

```kotlin
// Collect all state updates for debugging
lifecycleScope.launch {
    Appero.instance.shouldShowFeedbackPrompt.collect { value ->
        Log.d("Appero", "Feedback prompt state: $value")
    }
}
```

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
   - Run the `:sample` module
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
