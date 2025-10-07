# Changelog

All notable changes to the NEAR Kotlin Android App will be documented in this file.

## [2.0.0] - 2025-01-07

### 🎉 Major Update - Complete Architecture Overhaul

### Added

#### Architecture & State Management
- ✨ **MVVM Architecture**: Implemented proper ViewModel with StateFlow for reactive state management
- ✨ **Repository Pattern**: Clean separation of concerns with dedicated repository layer
- ✨ **Sealed Classes**: Comprehensive error handling with type-safe error states
- ✨ **Result Wrapper**: `NearResult<T>` sealed class for consistent success/error/loading states

#### New Models
- 📦 `NearError.kt` - Sealed class hierarchy for error handling
  - NetworkError, RpcError, ParseError, AuthError, TransactionError, Unknown
- 📦 `NearModels.kt` - Comprehensive data models
  - NetworkStatus, BlockInfo, ValidatorInfo, AccountInfo, TransactionStatus
  - WalletState, RpcEndpoint sealed class
  - QueryRequest, ContractCallParams

#### Enhanced Repository
- 🔄 **15+ RPC Endpoints**: Expanded from 4 to 15+ endpoints
  - `getStatus()` - Node status and sync information
  - `getBlock()` - Block details with finality parameter
  - `getGasPrice()` - Gas prices with optional block ID
  - `getNetworkInfo()` - Network connection status
  - `getValidators()` - Active validators list
  - `getHealth()` - RPC node health check
  - `getProtocolConfig()` - Protocol-level parameters
  - `getGenesisConfig()` - Genesis block configuration
  - `getChunk()` - Chunk details by hash
  - `getChanges()` - State changes in block
  - `queryAccount()` - Query account information
  - `callViewMethod()` - Call contract view methods
  - `getTransactionStatus()` - Transaction status lookup
  - `getClientConfig()` - Client node configuration
  - `getLightClientProof()` - Light client proofs

#### New UI Components
- 🎨 **MainScreen.kt** - Complete UI rebuild with modern Compose patterns
  - WalletSection - Enhanced wallet connection UI
  - RpcEndpointSection - Improved endpoint selection
  - AdditionalActionsSection - New advanced features section
  - ResultsSection - Better result display with error states
  - AccountQueryDialog - Interactive account query
  - ContractCallDialog - Contract method call interface

#### ViewModel Features
- 🧠 **NearViewModel.kt** - Centralized state management
  - `fetchEndpointData()` - Fetch any RPC endpoint
  - `queryAccount()` - Query account information
  - `callViewMethod()` - Call contract view methods
  - `getTransactionStatus()` - Check transaction status
  - `connectWallet()` - Connect wallet with state update
  - `disconnectWallet()` - Disconnect and reset state
  - `clearError()` - Clear error messages
  - StateFlow for reactive UI updates

#### Testing
- 🧪 **NearViewModelTest.kt** - Unit tests for ViewModel
  - State initialization tests
  - Wallet connection/disconnection tests
  - Endpoint selection tests
  - Error handling tests

### Changed

#### Updated Dependencies
- ⬆️ **near-jsonrpc-kotlin-client**: v1.0.0 → **v1.1.3**
  - Latest features and bug fixes
  - Improved type safety
  - Better error handling

#### Refactored Files
- 🔧 **MainActivity.kt**: Simplified with ViewModel integration
  - Removed direct repository usage
  - Proper lifecycle management
  - Better deep link handling
- 🔧 **Repository Structure**: Moved to `repository/` package
  - Better organization
  - Comprehensive error handling
  - Type-safe API calls

### Improved

#### Error Handling
- 🛡️ **Comprehensive Error Types**:
  - Network errors with retry suggestions
  - RPC errors with error codes
  - Parse errors for invalid data
  - Authentication errors
  - Transaction-specific errors
  - User-friendly error messages

#### State Management
- 📊 **Reactive State Updates**:
  - Loading states for all operations
  - Error states with detailed messages
  - Success states with data
  - Wallet connection state
  - Last endpoint tracking

#### User Experience
- 💫 **Loading Indicators**: Visual feedback for all async operations
- 💫 **Error Messages**: Clear, actionable error messages
- 💫 **Dialogs**: Interactive dialogs for account queries and contract calls
- 💫 **Better Layout**: Improved spacing and visual hierarchy

#### Code Quality
- 📝 **Documentation**: Comprehensive inline documentation
- 📝 **Type Safety**: Full Kotlin type safety throughout
- 📝 **Clean Code**: Following Android best practices
- 📝 **SOLID Principles**: Clean architecture patterns

### Documentation

- 📚 **Updated README.md**: Complete rewrite with:
  - Architecture diagram
  - Feature list
  - Setup instructions
  - API reference
  - Usage examples
  - Testing guide
  - Troubleshooting section

- 📚 **Added CHANGELOG.md**: This file for tracking changes

### Technical Details

#### Package Structure
```
com.psianturi.near_kotlin/
├── model/
│   ├── NearError.kt
│   └── NearModels.kt
├── repository/
│   └── NearRepository.kt
├── viewmodel/
│   └── NearViewModel.kt
├── ui/
│   └── screens/
│       └── MainScreen.kt
├── MainActivity.kt
└── [deprecated files...]
```

#### Deprecated Files
- ⚠️ `AppScreen.kt` - Replaced by `ui/screens/MainScreen.kt`
- ⚠️ `NearRepository.kt` (root) - Replaced by `repository/NearRepository.kt`

### Migration Guide

For existing code using the old architecture:

#### Before (v1.x):
```kotlin
val repo = NearRepository()
var info by remember { mutableStateOf("") }
scope.launch {
    info = repo.getNetworkInfo()?.toString() ?: "Error"
}
```

#### After (v2.0):
```kotlin
val viewModel: NearViewModel by viewModels()
val rpcResult by viewModel.rpcResult.collectAsStateWithLifecycle()

viewModel.fetchEndpointData(RpcEndpoint.NetworkInfo)

when (rpcResult) {
    is NearResult.Loading -> LoadingView()
    is NearResult.Success -> TextView(rpcResult.data)
    is NearResult.Error -> ErrorView(rpcResult.error)
}
```

### Breaking Changes

- ⚠️ Repository now returns `NearResult<T>` instead of nullable types
- ⚠️ Direct repository instantiation discouraged (use ViewModel)
- ⚠️ State management moved to ViewModel with StateFlow
- ⚠️ UI components now expect ViewModel instead of Repository

### Performance Improvements

- ⚡ Coroutine-based async operations with proper scope management
- ⚡ StateFlow instead of LiveData for better Compose integration
- ⚡ Efficient state updates with immutable data classes
- ⚡ Proper resource cleanup in ViewModel.onCleared()

### Security Improvements

- 🔒 Proper deep link validation
- 🔒 Safe error message handling (no sensitive data exposure)
- 🔒 Secure wallet callback handling

### Known Issues

- ⚠️ Transaction signing not yet implemented (planned for v2.1)
- ⚠️ Multi-network switching not available (planned for v2.2)
- ⚠️ Token management features coming in v2.3

### What's Next (v2.1)

- 🔮 Transaction signing and submission
- 🔮 Multi-network support (Testnet/Mainnet switching)
- 🔮 Enhanced contract interaction (change methods)
- 🔮 Transaction history view
- 🔮 Better offline support

---

## [1.0.0] - 2024-09-19

### Initial Release

- Basic NEAR RPC integration
- Wallet connection
- 4 RPC endpoints (network_info, status, block, gas_price)
- Basic Compose UI
- near-jsonrpc-kotlin-client v1.0.0

---

**Note**: This project follows [Semantic Versioning](https://semver.org/).