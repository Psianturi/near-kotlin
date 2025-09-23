# NEAR Android Demo App

A modern Android application showcasing NEAR blockchain integration with a clean, intuitive interface.

## 🚀 Features

- **Wallet Connection**: Seamless NEAR Testnet wallet integration
- **Network Monitoring**: Real-time NEAR network status and information
- **RPC Integration**: Direct access to NEAR JSON-RPC endpoints
- **Modern UI**: Built with Jetpack Compose and Material 3
- **Type Safety**: Full Kotlin type safety with generated models

## 📸 Screenshots

<img src="Screenshot_20250919_134111.png" alt="NEAR Android Demo App" width="300">


## �️ Tech Stack

- **Kotlin** - Modern Android development
- **Jetpack Compose** - Declarative UI framework
- **Ktor** - HTTP client for network requests
- **NEAR JSON-RPC Kotlin Client** - Type-safe NEAR blockchain integration

## 📦 Dependencies

### NEAR JSON-RPC Kotlin Client

```kotlin
// Add to build.gradle.kts (app level)
dependencies {
    implementation("com.github.Psianturi.near-jsonrpc-kotlin-client:near-jsonrpc-client:v1.0.0")
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
- JDK 17 or later
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

## Acknowledgments

- NEAR Protocol for the blockchain infrastructure
- Jetpack Compose for modern Android UI
- Ktor for HTTP client functionality
- Kotlinx.serialization for data serialization

## Using NEAR JSON-RPC Kotlin Client in Your Project

---

**Happy coding with NEAR! 🚀✨**