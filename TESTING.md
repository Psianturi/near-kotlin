# Testing Guide for NEAR Android Demo App

This guide covers comprehensive testing strategies for the NEAR Android application.

## 🧪 Testing Overview

### Test Types
- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test component interactions
- **UI Tests**: Test user interface and user flows
- **Manual Tests**: Exploratory testing and edge cases

## 🛠️ Unit Testing

### Repository Testing

```kotlin
// NearRepositoryTest.kt
class NearRepositoryTest {

    private lateinit var repository: NearRepository

    @Before
    fun setup() {
        // Mock HttpClient and setup repository
    }

    @Test
    fun `getNetworkInfo returns formatted string on success`() = runTest {
        // Given
        val mockResponse = """{"peers": [], "known_producers": []}"""

        // When
        val result = repository.getNetworkInfo()

        // Then
        assertTrue(result.contains("peers"))
    }

    @Test
    fun `getNetworkInfo returns error message on failure`() = runTest {
        // Given - network failure scenario

        // When
        val result = repository.getNetworkInfo()

        // Then
        assertTrue(result.contains("Error"))
    }
}
```

### ViewModel Testing

```kotlin
// MainViewModelTest.kt
class MainViewModelTest {

    @Test
    fun `wallet connection updates accountId state`() {
        // Test wallet connection state management
    }

    @Test
    fun `network info loading shows loading state`() {
        // Test loading state during API calls
    }
}
```

## 🔗 Integration Testing

### RPC Integration Tests

```kotlin
// NearRpcIntegrationTest.kt
class NearRpcIntegrationTest {

    @Test
    fun `real NEAR testnet integration test`() = runTest {
        val repository = NearRepository()

        // Test actual network calls (requires internet)
        val networkInfo = repository.getNetworkInfo()

        assertNotNull(networkInfo)
        assertFalse(networkInfo.contains("Error"))
    }

    @Test
    fun `test network permissions are granted`() {
        // This test would require device/emulator with proper permissions
        // In real scenario, test INTERNET permission programmatically
        val context = ApplicationProvider.getApplicationContext<Context>()
        val permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.INTERNET
        )
        assertEquals(PackageManager.PERMISSION_GRANTED, permission)
    }
}
```

### Wallet Integration Tests

```kotlin
// WalletIntegrationTest.kt
class WalletIntegrationTest {

    @Test
    fun `wallet login URL generation is correct`() {
        val repository = NearRepository()
        val loginUrl = repository.getWalletLoginUrl()

        assertTrue(loginUrl.toString().contains("wallet.testnet.near.org"))
        assertTrue(loginUrl.toString().contains("success_url"))
        assertTrue(loginUrl.toString().contains("failure_url"))
    }
}
```

## 📱 UI Testing

### Compose UI Tests

```kotlin
// AppScreenTest.kt
class AppScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `wallet connection button is displayed when not connected`() {
        composeTestRule.setContent {
            AppScreen(NearRepository(), null)
        }

        composeTestRule.onNodeWithText("Connect Wallet")
            .assertIsDisplayed()
    }

    @Test
    fun `connected account is displayed when wallet is connected`() {
        val accountId = "testuser.testnet"

        composeTestRule.setContent {
            AppScreen(NearRepository(), accountId)
        }

        composeTestRule.onNodeWithText("Connected: $accountId")
            .assertIsDisplayed()
    }

    @Test
    fun `network info fetch shows loading indicator`() {
        composeTestRule.setContent {
            AppScreen(NearRepository(), null)
        }

        // Click fetch button
        composeTestRule.onNodeWithText("Fetch Network Info")
            .performClick()

        // Verify loading state
        composeTestRule.onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }
}
```

## 🏃 Running Tests

### Command Line

```bash
# Run all unit tests
./gradlew testDebugUnitTest

# Run specific test class
./gradlew testDebugUnitTest --tests "*NearRepositoryTest*"

# Run with coverage
./gradlew testDebugUnitTest jacocoTestReport

# Run integration tests (requires device/emulator)
./gradlew connectedDebugAndroidTest

# Run UI tests
./gradlew connectedDebugAndroidTest --tests "*AppScreenTest*"
```

### Android Studio

1. **Run Tests**: Right-click on test class → Run
2. **Debug Tests**: Right-click on test class → Debug
3. **Coverage**: Run → Run with Coverage
4. **Test Results**: View → Tool Windows → Run

## 🔍 Manual Testing Checklist

### Permission Testing

- [ ] **INTERNET Permission**: Verify permission is granted in device settings
- [ ] **Network Access**: Test with WiFi and mobile data
- [ ] **Permission Dialog**: Check if permission prompt appears on first run
- [ ] **Permission Denied**: Test behavior when permission is denied
- [ ] **Permission Revoked**: Test after revoking permission in settings

### Wallet Connection Testing

- [ ] Tap "Connect Wallet" button
- [ ] Verify Custom Tabs opens NEAR wallet
- [ ] Test successful login flow
- [ ] Test login cancellation
- [ ] Test invalid credentials
- [ ] Verify callback handling
- [ ] Check account ID display after login
- [ ] Test disconnect functionality

### Network Data Testing

- [ ] Test all RPC endpoints (network_info, status, block, gas_price)
- [ ] Verify loading states
- [ ] Test error handling (network offline, invalid responses)
- [ ] Check data formatting and display
- [ ] Test rapid successive requests
- [ ] Verify timeout handling

### UI/UX Testing

- [ ] Test on different screen sizes
- [ ] Test dark/light theme compatibility
- [ ] Verify accessibility features
- [ ] Test orientation changes
- [ ] Check keyboard navigation
- [ ] Verify error message clarity

### Edge Cases

- [ ] Test with slow network connection
- [ ] Test with no internet connection
- [ ] Test with invalid wallet URLs
- [ ] Test with malformed API responses
- [ ] Test memory pressure scenarios
- [ ] Test app backgrounding during API calls

## 🐛 Debugging Tests

### Common Issues

1. **Permission Denied Error**
   ```kotlin
   // If you get "Permission denied (missing INTERNET permission?)"
   // SOLUTION: Ensure AndroidManifest.xml includes:
   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

   // Also check network_security_config.xml for API 28+
   ```

2. **Test Timeout**
   ```kotlin
   @Test(timeout = 5000)
   fun `test with custom timeout`() {
       // Test code
   }
   ```

3. **Async Testing**
   ```kotlin
   @Test
   fun `async test with runTest`() = runTest {
       val result = repository.getNetworkInfo()
       assertNotNull(result)
   }
   ```

4. **UI Test Synchronization**
   ```kotlin
   composeTestRule.waitUntil(timeoutMillis = 5000) {
       composeTestRule.onAllNodesWithText("Result")
           .fetchSemanticsNodes().isNotEmpty()
   }
   ```

## 📊 Test Coverage

### Coverage Goals

- **Unit Tests**: 80%+ coverage
- **Integration Tests**: Key user flows covered
- **UI Tests**: Main screens and interactions

### Coverage Report

```bash
# Generate coverage report
./gradlew jacocoTestReport

# View report in browser
# app/build/reports/jacoco/html/index.html
```

## 🚀 CI/CD Testing

### GitHub Actions Workflow

```yaml
name: Android CI
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'

      - name: Cache Gradle packages
        uses: actions/cache@v3
        with:
          path: ~/.gradle/caches
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest

      - name: Build APK
        run: ./gradlew assembleDebug

      - name: Upload test results
        uses: actions/upload-artifact@v3
        if: always()
        with:
          name: test-results
          path: app/build/reports/tests/
```

## 📱 Device Testing

### Emulator Testing

```bash
# List available emulators
emulator -list-avds

# Start specific emulator
emulator -avd Pixel_8_API_34

# Run tests on emulator
./gradlew connectedDebugAndroidTest
```

### Physical Device Testing

1. **Enable Developer Options**
2. **Enable USB Debugging**
3. **Allow USB Debugging** when prompted
4. **Verify device connection**: `adb devices`
5. **Run tests**: `./gradlew connectedDebugAndroidTest`

## 🔧 Test Utilities

### Test Helpers

```kotlin
// TestUtils.kt
object TestUtils {
    fun createMockRepository(): NearRepository {
        // Create repository with mocked dependencies
    }

    fun createTestWalletCallback(accountId: String): Uri {
        return Uri.parse("myapp://callback?account_id=$accountId")
    }
}
```

### Mock Data

```kotlin
// MockData.kt
object MockData {
    val sampleNetworkInfo = """
        {
            "peers": [
                {"id": "peer1", "addr": "127.0.0.1:24567"}
            ],
            "known_producers": [
                {"account_id": "validator1.testnet", "peer_id": "peer1"}
            ]
        }
    """.trimIndent()
}
```

## 📈 Performance Testing

### Network Performance

```kotlin
@Test
fun `network request performance test`() = runTest {
    val startTime = System.currentTimeMillis()

    repository.getNetworkInfo()

    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime

    assertTrue(duration < 5000) // Should complete within 5 seconds
}
```

### Memory Testing

```kotlin
@Test
fun `memory leak test for repository`() {
    // Test repository cleanup
    val repository = NearRepository()

    // Perform operations
    runTest {
        repeat(100) {
            repository.getNetworkInfo()
        }
    }

    // Verify cleanup
    repository.close()
}
```

## 🎯 Best Practices

### Test Organization

1. **Arrange-Act-Assert** pattern
2. **Descriptive test names**
3. **Single responsibility per test**
4. **Independent tests** (no shared state)
5. **Fast execution** (avoid slow operations)

### Test Maintenance

1. **Regular test runs** in CI/CD
2. **Update tests** when code changes
3. **Remove obsolete tests**
4. **Document test scenarios**
5. **Monitor test coverage**

---

**Happy Testing! 🧪✨**