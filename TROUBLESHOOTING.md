# Android Build Troubleshooting Guide

## Issue: "No connected devices!" Error

### Problem:
```
FAILURE: Build failed with an exception.
* What went wrong:
Execution failed for task ':app:installDebug'.
> com.android.builder.testing.api.DeviceException: No connected devices!
```

### Solutions:

#### Solution 1: Use Android Emulator (Recommended)

1. **Open Android Studio**
2. **Go to Device Manager:**
   - Click on "Device Manager" in the toolbar
   - Or: View → Tool Windows → Device Manager

3. **Create Virtual Device:**
   - Click "Create device"
   - Choose a device (e.g., "Pixel 8 Pro")
   - Select system image (API 35 recommended)
   - Click "Next" → "Finish"

4. **Start Emulator:**
   - Click the "Play" button next to your virtual device
   - Wait for emulator to fully boot up (may take 2-3 minutes)

5. **Retry Build:**
   ```bash
   ./gradlew installDebug
   ```

#### Solution 2: Use Physical Android Device

1. **Enable Developer Options:**
   - Go to Settings → About Phone
   - Tap "Build number" 7 times until you see "You are now a developer"

2. **Enable USB Debugging:**
   - Go to Settings → Developer Options
   - Enable "USB debugging"
   - Enable "Install via USB" (if available)

3. **Connect Device:**
   - Connect your Android device via USB
   - Allow USB debugging when prompted
   - Check connection: `adb devices`

4. **Retry Build:**
   ```bash
   ./gradlew installDebug
   ```

#### Solution 3: Build APK Only (No Installation)

If you don't want to install immediately:

```bash
# Build debug APK
./gradlew assembleDebug

# Find APK location
ls -la app/build/outputs/apk/debug/
# Output: app-debug.apk

# Install manually later
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### Solution 4: Check Device Connection

```bash
# List connected devices
adb devices

# Expected output:
# List of devices attached
# emulator-5554   device
# R58N123ABCD     device

# If no devices shown, restart ADB
adb kill-server
adb start-server
adb devices
```

## Other Common Issues

### Issue: "INSTALL_FAILED_INSUFFICIENT_STORAGE"

**Solution:**
```bash
# Check device storage
adb shell df

# List installed apps
adb shell pm list packages

# Uninstall unused apps
adb uninstall <package_name>

# Clear app data
adb shell pm clear <package_name>
```

### Issue: "INSTALL_FAILED_UPDATE_INCOMPATIBLE"

**Solution:**
```bash
# Uninstall existing app first
adb uninstall com.psianturi.near_kotlin

# Then install new version
./gradlew installDebug
```

### Issue: "INSTALL_FAILED_VERIFICATION_FAILURE"

**Solution:**
```bash
# Disable verification temporarily
adb shell settings put global verifier_verify_adb_installs 0

# Install app
./gradlew installDebug

# Re-enable verification
adb shell settings put global verifier_verify_adb_installs 1
```

### Issue: "INSTALL_FAILED_DEXOPT"

**Solution:**
```bash
# Clear device cache
adb shell pm clear com.psianturi.near_kotlin

# Restart device
adb reboot

# Try installation again
./gradlew installDebug
```

## Build Issues

### Issue: Gradle Build Fails

**Solutions:**
```bash
# Clean build
./gradlew clean

# Clear Gradle cache
rm -rf ~/.gradle/caches/

# Invalidate Android Studio cache
# File → Invalidate Caches / Restart

# Rebuild
./gradlew build
```

### Issue: Kotlin Compilation Errors

**Solutions:**
```bash
# Check Kotlin version compatibility
./gradlew kotlinVersion

# Update Kotlin version in build.gradle.kts
kotlin("multiplatform") version "1.9.20"

# Clean and rebuild
./gradlew clean build
```

### Issue: Dependency Resolution Fails

**Solutions:**
```bash
# Refresh dependencies
./gradlew --refresh-dependencies

# Clear dependency cache
rm -rf ~/.gradle/caches/modules-2/

# Check network connectivity
ping google.com
```

## Testing Issues

### Issue: Unit Tests Fail

**Solutions:**
```bash
# Run specific test
./gradlew testDebugUnitTest --tests "*NearServiceTest*"

# Run with stacktrace
./gradlew testDebugUnitTest --stacktrace

# Run in verbose mode
./gradlew testDebugUnitTest --info
```

### Issue: Instrumentation Tests Fail

**Solutions:**
```bash
# Check device connection
adb devices

# Run tests on device
./gradlew connectedDebugAndroidTest

# Run specific test
./gradlew connectedDebugAndroidTest --tests "*TestClass*"
```

## Performance Issues

### Issue: Slow Build Times

**Solutions:**
```bash
# Enable Gradle daemon
echo "org.gradle.daemon=true" >> ~/.gradle/gradle.properties

# Enable parallel builds
echo "org.gradle.parallel=true" >> ~/.gradle/gradle.properties

# Increase memory
echo "org.gradle.jvmargs=-Xmx4096m" >> ~/.gradle/gradle.properties
```

### Issue: Emulator Performance

**Solutions:**
```bash
# Use hardware acceleration
# Enable VT-x/AMD-V in BIOS

# Use x86 emulator image
# Select x86 system image in AVD

# Increase emulator RAM
# Set RAM to 2048MB or higher in AVD settings
```

## Network Issues

### Issue: "Permission denied (missing INTERNET permission?)" - FIXED ✅

**Problem:**
```
java.lang.SecurityException: Permission denied (missing INTERNET permission?)
java.net.Inet6AddressImpl.lookupHostByName(Inet6AddressImpl.java:150)
```

**Solution Applied:**
✅ **INTERNET permission already added to AndroidManifest.xml:**
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

**If Error Persists:**
```bash
# Clean and rebuild project
./gradlew clean
./gradlew assembleDebug

# Check if permission is granted in device settings
# Settings → Apps → [Your App] → Permissions → Allow INTERNET

# Restart Android Studio
# File → Invalidate Caches / Restart

# Try different device/emulator
```

### Issue: NEAR API Calls Fail

**Solutions:**
```kotlin
// Check network permissions in AndroidManifest.xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

// Add network security config for API 28+
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

**Create network_security_config.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">rpc.testnet.near.org</domain>
        <domain includeSubdomains="true">rpc.mainnet.near.org</domain>
        <domain includeSubdomains="true">wallet.testnet.near.org</domain>
        <domain includeSubdomains="true">wallet.mainnet.near.org</domain>
    </domain-config>
</network-security-config>
```

### Issue: Network Connection Timeout

**Solutions:**
```bash
# Check internet connectivity
ping google.com

# Test NEAR RPC endpoint directly
curl https://rpc.testnet.near.org

# Check device network settings
# Settings → Network & Internet → WiFi/Mobile Data

# Try different network (WiFi vs Mobile Data)
```

### Issue: SSL/HTTPS Certificate Issues

**Solutions:**
```xml
<!-- Add to network_security_config.xml -->
<base-config cleartextTrafficPermitted="false">
    <trust-anchors>
        <certificates src="system" />
        <certificates src="user" />
    </trust-anchors>
</base-config>
```

## Quick Commands Reference

```bash
# Build commands
./gradlew assembleDebug          # Build APK only
./gradlew installDebug           # Build and install
./gradlew build                  # Full build

# Clean commands
./gradlew clean                  # Clean build
rm -rf ~/.gradle/caches/         # Clear Gradle cache

# Device commands
adb devices                      # List devices
adb kill-server                  # Restart ADB
adb install <path/to/apk>        # Manual install

# Test commands
./gradlew test                   # Unit tests
./gradlew connectedAndroidTest   # Integration tests
```

## Getting Help

1. **Check Logs:**
   ```bash
   # Android Studio logs
   # View → Tool Windows → Logcat

   # Gradle logs
   ./gradlew build --info --stacktrace
   ```

2. **Community Resources:**
   - Stack Overflow: Search for specific error messages
   - Android Developers: Official documentation
   - Kotlin Slack: Community support

3. **Report Issues:**
   - Include full error logs
   - Specify Android Studio version
   - Include device/emulator details
   - Share build.gradle.kts content