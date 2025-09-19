# NEAR Android Demo App

A modern Android application showcasing NEAR blockchain integration with a clean, intuitive interface.

## 🚀 Features

- **Wallet Connection**: Seamless NEAR Testnet wallet integration
- **Network Monitoring**: Real-time NEAR network status and information
- **RPC Integration**: Direct access to NEAR JSON-RPC endpoints
- **Modern UI**: Built with Jetpack Compose and Material 3
- **Type Safety**: Full Kotlin type safety with generated models

## 🛠️ Tech Stack

- **Kotlin** - Modern Android development
- **Jetpack Compose** - Declarative UI framework
- **Ktor** - HTTP client for network requests
- **NEAR JSON-RPC Kotlin Client** - Type-safe NEAR blockchain integration

## 📦 Dependencies

### NEAR JSON-RPC Kotlin Client

```kotlin
// Add to build.gradle.kts (app level)
dependencies {
    implementation("com.github.Psianturi:near-jsonrpc-kotlin-client:1.0.0")
}
```

### Repository Setup

```kotlin
// Add to settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or later
- Android device/emulator (API 24+)

### Quick Start

1. **Clone and Open**
   ```bash
   git clone <repository-url>
   cd nearkotlin
   ```

2. **Open in Android Studio**
   - Import project
   - Wait for Gradle sync to complete

3. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

4. **Grant Permissions**
   - Allow internet access when prompted
   - The app will request necessary permissions automatically

## 📱 Usage

### Wallet Connection
Tap "Connect Wallet" to authenticate with NEAR Testnet wallet via Custom Tabs.

### Network Data
Select RPC endpoints to fetch real-time NEAR network information including status, blocks, and gas prices.


## 🔗 Deep Link Configuration

The app handles wallet authentication callbacks via deep links:

- **Scheme**: `myapp`
- **Host**: `callback`
- **Example URL**: `myapp://callback?account_id=user.testnet&public_key=...`

## 🌐 Network Configuration

For Android API 28+, cleartext traffic is configured in `network_security_config.xml`:

```xml
<domain-config cleartextTrafficPermitted="true">
    <domain includeSubdomains="true">rpc.testnet.near.org</domain>
    <domain includeSubdomains="true">rpc.mainnet.near.org</domain>
    <domain includeSubdomains="true">wallet.testnet.near.org</domain>
    <domain includeSubdomains="true">wallet.mainnet.near.org</domain>
</domain-config>
```

## 🧪 Testing

### Unit Tests

```bash
./gradlew testDebugUnitTest
```

### Integration Tests

```bash
./gradlew connectedDebugAndroidTest
```

### Manual Testing

1. **Wallet Connection Test:**
   - Tap "Connect Wallet"
   - Verify Custom Tabs opens NEAR wallet
   - Check callback handling after login

2. **RPC Calls Test:**
   - Select different endpoints
   - Verify data fetching works
   - Check error handling


## 📚 API Reference

### NEAR JSON-RPC Kotlin Client Usage

#### Basic Setup

```kotlin
// 1. Setup HTTP client
val httpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        })
    }
}

// 2. Create JsonRpcTransport
val transport = JsonRpcTransport(
    client = httpClient,
    rpcUrl = "https://rpc.testnet.near.org"
)

// 3. Create NearRpcClient
val nearClient = NearRpcClient(transport)

// 4. Use RPC methods
val networkInfo = nearClient.networkInfo()
val status = nearClient.status()
val block = nearClient.block()
val gasPrice = nearClient.gasPrice()
```

#### Available RPC Methods

| Method | Description | Return Type |
|--------|-------------|-------------|
| `networkInfo()` | Get network information and peer details | `RpcNetworkInfoResponse` |
| `status()` | Get node status and sync information | `RpcStatusResponse` |
| `block()` | Get latest block information | `RpcBlockResponse` |
| `gasPrice()` | Get current gas price | `RpcGasPriceResponse` |
| `query()` | Query account/contract state | `RpcQueryResponse` |
| `sendTx()` | Send transaction | `RpcTransactionResponse` |
| `validators()` | Get validator information | `RpcValidatorResponse` |

#### Advanced Usage with Parameters

```kotlin
// Query account state
val queryRequest = RpcQueryRequest(
    requestType = "view_account",
    accountId = "example.testnet",
    finality = "final"
)
val accountInfo = nearClient.query(params = queryRequest)

// Get specific block
val blockRequest = BlockReference(finality = "final")
val block = nearClient.block(params = blockRequest)

// Get gas price for specific block
val gasPriceRequest = RpcGasPriceRequest(blockId = 12345)
val gasPrice = nearClient.gasPrice(params = gasPriceRequest)
```

#### Error Handling

```kotlin
try {
    val result = nearClient.networkInfo()
    // Process result
} catch (e: Exception) {
    when (e) {
        is JsonRpcException -> {
            // JSON-RPC specific error
            println("RPC Error: ${e.error.code} - ${e.error.message}")
        }
        is IOException -> {
            // Network error
            println("Network Error: ${e.message}")
        }
        else -> {
            // Other errors
            println("Error: ${e.message}")
        }
    }
}
```

### Wallet Integration

#### Wallet Connection Flow

```kotlin
// Generate wallet login URL
fun getWalletLoginUrl(): Uri {
    val successUrl = "myapp://callback"
    val failureUrl = "myapp://callback?error=true"

    return Uri.parse("https://wallet.testnet.near.org/login/")
        .buildUpon()
        .appendQueryParameter("success_url", successUrl)
        .appendQueryParameter("failure_url", failureUrl)
        .appendQueryParameter("contract_id", "your-contract.testnet")
        .build()
}

// Handle wallet callback
fun handleWalletCallback(uri: Uri): String? {
    return uri.getQueryParameter("account_id")
}
```

#### Deep Link Configuration

```xml
<!-- AndroidManifest.xml -->
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="myapp" android:host="callback" />
</intent-filter>
```

- **Login URL**: `https://wallet.testnet.near.org/login/`
- **Success Callback**: `myapp://callback?account_id={account_id}`
- **Error Callback**: `myapp://callback?error={error_message}`



## 🙏 Acknowledgments

- NEAR Protocol for the blockchain infrastructure
- Jetpack Compose for modern Android UI
- Ktor for HTTP client functionality
- Kotlinx.serialization for data serialization

## 🚀 Using NEAR JSON-RPC Kotlin Client in Your Project

### For Other Android Projects

#### 1. Add Library Dependency

**Option A: JitPack (Recommended for published library)**
```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

// build.gradle.kts (app level)
dependencies {
    // JitPack dependency for published library
    implementation("com.github.Psianturi:near-jsonrpc-kotlin-client:1.0.0")
}
```

**📝 How JitPack Works:**
- **GitHub Repository**: `https://github.com/Psianturi/near-jsonrpc-kotlin-client`
- **JitPack Group ID**: `com.github.Psianturi` (auto-generated from GitHub username)
- **Artifact ID**: `near-jsonrpc-kotlin-client` (same as repository name)
- **Version**: `1.0.0` (Git tag, not package.json version)

**Option B: Local Composite Build (For development)**
```kotlin
// settings.gradle.kts
include(":near-jsonrpc-kotlin-client")
project(":near-jsonrpc-kotlin-client").projectDir = file("../near-jsonrpc-kotlin-client")

// build.gradle.kts (app level)
dependencies {
    implementation(project(":near-jsonrpc-kotlin-client:packages:client"))
    implementation(project(":near-jsonrpc-kotlin-client:packages:types"))
}
```

#### 2. Setup Android Configuration

**AndroidManifest.xml:**
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- Network permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:networkSecurityConfig="@xml/network_security_config"
        ...>
        <!-- Wallet deep link (optional) -->
        <intent-filter android:autoVerify="true">
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="yourapp" android:host="callback" />
        </intent-filter>
    </application>
</manifest>
```

**Network Security Config (res/xml/network_security_config.xml):**
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

#### 3. Import Statements in Kotlin Code

**⚠️ IMPORTANT**: Script berikut digunakan di **file Kotlin** (bukan di build.gradle.kts):

```kotlin
// Di file Kotlin (.kt), bukan di build.gradle.kts
import com.near.jsonrpc.client.NearRpcClient      // Main client class
import com.near.jsonrpc.JsonRpcTransport          // Transport layer
// import com.near.jsonrpc.types.*                 // Generated types (opsional)
```

#### 4. Basic Usage Example

```kotlin
// File: NearRepository.kt
package com.yourcompany.yourapp

// ✅ IMPORT STATEMENTS (di sini, bukan di build.gradle.kts)
import com.near.jsonrpc.JsonRpcTransport
import com.near.jsonrpc.client.NearRpcClient
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class NearRepository {

    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    private val transport = JsonRpcTransport(
        client = httpClient,
        rpcUrl = "https://rpc.testnet.near.org"
    )

    private val nearClient = NearRpcClient(transport)

    suspend fun getNetworkInfo(): RpcNetworkInfoResponse {
        return nearClient.networkInfo()
    }

    suspend fun getAccountInfo(accountId: String): RpcQueryResponse {
        val queryRequest = RpcQueryRequest(
            requestType = "view_account",
            accountId = accountId,
            finality = "final"
        )
        return nearClient.query(params = queryRequest)
    }

    suspend fun getLatestBlock(): RpcBlockResponse {
        val blockRequest = BlockReference(finality = "final")
        return nearClient.block(params = blockRequest)
    }

    fun close() {
        httpClient.close()
    }
}
```

#### 4. Compose UI Integration

```kotlin
@Composable
fun NearDashboard(repository: NearRepository) {
    var networkInfo by remember { mutableStateOf<RpcNetworkInfoResponse?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(
            onClick = {
                // Launch coroutine to fetch data
                // Handle loading states and errors
            },
            enabled = !isLoading
        ) {
            Text("Fetch Network Info")
        }

        // Display results
        networkInfo?.let { info ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Network Info", style = MaterialTheme.typography.titleMedium)
                    Text("Peers: ${info.result.numPeers}")
                    Text("Known Producers: ${info.result.knownProducers.size}")
                }
            }
        }
    }
}
```

#### 5. Wallet Integration (Optional)

```kotlin
// Wallet connection helper
fun createWalletLoginUrl(
    contractId: String,
    successUrl: String = "yourapp://callback",
    failureUrl: String = "yourapp://callback?error=true"
): Uri {
    return Uri.parse("https://wallet.testnet.near.org/login/")
        .buildUpon()
        .appendQueryParameter("success_url", successUrl)
        .appendQueryParameter("failure_url", failureUrl)
        .appendQueryParameter("contract_id", contractId)
        .build()
}

// Handle wallet callback in Activity
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    intent.data?.let { uri ->
        val accountId = uri.getQueryParameter("account_id")
        val error = uri.getQueryParameter("error")

        if (accountId != null) {
            // Wallet connected successfully
            println("Connected: $accountId")
        } else if (error != null) {
            // Handle error
            println("Wallet error: $error")
        }
    }
}
```

### Available RPC Methods

| Method | Parameters | Description |
|--------|------------|-------------|
| `networkInfo()` | None | Get network information and peer details |
| `status()` | None | Get node status and sync information |
| `block(params)` | `BlockReference` | Get block information |
| `gasPrice(params)` | `RpcGasPriceRequest` | Get gas price for block |
| `query(params)` | `RpcQueryRequest` | Query account/contract state |
| `sendTx(params)` | `RpcSendTransactionRequest` | Send transaction |
| `validators(params)` | `RpcValidatorsOrderedRequest` | Get validator information |
| `tx(params)` | `RpcTransactionStatusRequest` | Get transaction status |

### Error Handling

```kotlin
try {
    val result = nearClient.networkInfo()
    // Process successful result
} catch (e: Exception) {
    when (e) {
        is JsonRpcException -> {
            // JSON-RPC protocol error
            println("RPC Error ${e.error.code}: ${e.error.message}")
        }
        is IOException -> {
            // Network connectivity error
            println("Network Error: ${e.message}")
        }
        is SerializationException -> {
            // JSON parsing error
            println("Data Error: ${e.message}")
        }
        else -> {
            // Other errors
            println("Unknown Error: ${e.message}")
        }
    }
}
```

### Best Practices

1. **Always close HTTP client** when done to prevent memory leaks
2. **Handle network errors gracefully** with user-friendly messages
3. **Use appropriate coroutine scopes** for UI updates
4. **Cache responses** when appropriate to reduce network calls
5. **Validate input parameters** before making RPC calls
6. **Test on real devices** for network-related functionality

### 📝 Dependency vs Import: Quick Reference

| Type | File | Purpose | Example |
|------|------|---------|---------|
| **Dependency** | `build.gradle.kts` | Download library JAR | `implementation("com.github.Psianturi:near-jsonrpc-kotlin-client:1.0.0")` |
| **Import** | `.kt` files | Use library classes | `import com.near.jsonrpc.client.NearRpcClient` |

**❌ Wrong - Don't put imports in build.gradle.kts:**
```kotlin
// build.gradle.kts - DEPENDENCIES only
dependencies {
    implementation("com.github.Psianturi:near-jsonrpc-kotlin-client:1.0.0")
    // ❌ Don't put import statements here!
}
```

**✅ Correct - Imports go in Kotlin files:**
```kotlin
// NearRepository.kt - IMPORTS here
import com.near.jsonrpc.client.NearRpcClient  // ✅ Correct
import com.near.jsonrpc.JsonRpcTransport      // ✅ Correct
```

---

## 📋 Quick Start Checklist

- [ ] ✅ Add library dependency (JitPack or local)
- [ ] ✅ Configure network permissions in AndroidManifest.xml
- [ ] ✅ Add network security config for API 28+
- [ ] ✅ Setup HTTP client with JSON serialization
- [ ] ✅ Create JsonRpcTransport instance
- [ ] ✅ Initialize NearRpcClient
- [ ] ✅ Make your first RPC call (try `networkInfo()`)
- [ ] ✅ Handle errors appropriately
- [ ] ✅ Test on device/emulator

## 🔍 Where to Use Import Statements in nearkotlin Project

**Script import berikut digunakan di file Kotlin dalam project nearkotlin:**

### 📁 File: `nearkotlin/app/src/main/java/com/psianturi/near_kotlin/NearRepository.kt`

```kotlin
package com.psianturi.near_kotlin

// ✅ IMPORT STATEMENTS digunakan di sini (baris 4-5)
import com.near.jsonrpc.JsonRpcTransport          // Transport layer
import com.near.jsonrpc.client.NearRpcClient      // Main client class

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

class NearRepository {
    // Implementation using imported classes
    private val transport = JsonRpcTransport(...)  // ✅ Menggunakan import
    private val client = NearRpcClient(transport)  // ✅ Menggunakan import
}
```

### 📁 File: `nearkotlin/app/src/main/java/com/psianturi/near_kotlin/MainActivity.kt`

```kotlin
package com.psianturi.near_kotlin

// ✅ IMPORT STATEMENTS juga digunakan di sini jika perlu
import com.near.jsonrpc.client.NearRpcClient
import android.os.Bundle
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    // Can use NearRpcClient here if needed
}
```

### 📁 File: `nearkotlin/app/src/main/java/com/psianturi/near_kotlin/AppScreen.kt`

```kotlin
package com.psianturi.near_kotlin

// ✅ IMPORT STATEMENTS digunakan di sini untuk Compose UI
import com.near.jsonrpc.client.NearRpcClient
import androidx.compose.runtime.*
import androidx.compose.material3.*

@Composable
fun AppScreen(repository: NearRepository) {
    // UI implementation
}
```

---

## 🎯 Summary: Dependency vs Import

| **DEPENDENCY** (build.gradle.kts) | **IMPORT** (Kotlin files) |
|-----------------------------------|---------------------------|
| Downloads library JAR from JitPack | Makes library classes available in code |
| `implementation("com.github.Psianturi:near-jsonrpc-kotlin-client:1.0.0")` | `import com.near.jsonrpc.client.NearRpcClient` |
| One time setup per project | Needed in every Kotlin file that uses the library |
| Makes library available to project | Makes specific classes available to file |

---

**Happy coding with NEAR! 🚀✨**