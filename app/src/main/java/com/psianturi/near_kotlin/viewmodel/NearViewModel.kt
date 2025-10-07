package com.psianturi.near_kotlin.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psianturi.near_kotlin.model.*
import com.psianturi.near_kotlin.repository.NearRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement

/**
 * ViewModel for NEAR blockchain operations with proper state management
 */
class NearViewModel(
    private val repository: NearRepository = NearRepository()
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(NearUiState())
    val uiState: StateFlow<NearUiState> = _uiState.asStateFlow()

    // Wallet State
    private val _walletState = MutableStateFlow(WalletState())
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    // RPC Result State
    private val _rpcResult = MutableStateFlow<NearResult<JsonElement>>(NearResult.Loading)
    val rpcResult: StateFlow<NearResult<JsonElement>> = _rpcResult.asStateFlow()

    /**
     * Fetch RPC endpoint data
     */
    fun fetchEndpointData(endpoint: RpcEndpoint) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            _rpcResult.value = NearResult.Loading

            val result = when (endpoint) {
                RpcEndpoint.NetworkInfo -> repository.getNetworkInfo()
                RpcEndpoint.Status -> repository.getStatus()
                RpcEndpoint.Block -> repository.getBlock()
                RpcEndpoint.GasPrice -> repository.getGasPrice()
                RpcEndpoint.Validators -> repository.getValidators()
                RpcEndpoint.Health -> repository.getHealth()
                RpcEndpoint.ProtocolConfig -> repository.getProtocolConfig()
                RpcEndpoint.GenesisConfig -> repository.getGenesisConfig()
                RpcEndpoint.Chunk -> {
                    // For chunk, we need a chunk hash - show error if not provided
                    NearResult.Error(NearError.ParseError("Chunk hash required for this endpoint"))
                }
                RpcEndpoint.Changes -> {
                    // For changes, we need a block ID - show error if not provided
                    NearResult.Error(NearError.ParseError("Block ID required for this endpoint"))
                }
            }

            _rpcResult.value = result
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.errorOrNull(),
                    lastEndpoint = endpoint.displayName
                )
            }
        }
    }

    /**
     * Query account information
     */
    fun queryAccount(accountId: String) {
        if (accountId.isBlank()) {
            _uiState.update {
                it.copy(error = NearError.ParseError("Account ID cannot be empty"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            _rpcResult.value = NearResult.Loading

            val result = repository.queryAccount(accountId)
            _rpcResult.value = result
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.errorOrNull(),
                    lastEndpoint = "Account Query: $accountId"
                )
            }
        }
    }

    /**
     * Call contract view method
     */
    fun callViewMethod(
        contractId: String,
        methodName: String,
        args: String = "{}"
    ) {
        if (contractId.isBlank() || methodName.isBlank()) {
            _uiState.update {
                it.copy(error = NearError.ParseError("Contract ID and method name are required"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            _rpcResult.value = NearResult.Loading

            val result = repository.callViewMethod(contractId, methodName, args)
            _rpcResult.value = result
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.errorOrNull(),
                    lastEndpoint = "View Method: $contractId.$methodName"
                )
            }
        }
    }

    /**
     * Get transaction status
     */
    fun getTransactionStatus(txHash: String, accountId: String) {
        if (txHash.isBlank() || accountId.isBlank()) {
            _uiState.update {
                it.copy(error = NearError.ParseError("Transaction hash and account ID are required"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            _rpcResult.value = NearResult.Loading

            val result = repository.getTransactionStatus(txHash, accountId)
            _rpcResult.value = result
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = result.errorOrNull(),
                    lastEndpoint = "Transaction Status"
                )
            }
        }
    }

    /**
     * Get wallet login URL
     */
    fun getWalletLoginUrl(contractId: String = "example-contract.testnet"): Uri {
        return repository.getWalletLoginUrl(contractId)
    }

    /**
     * Connect wallet with account ID
     */
    fun connectWallet(accountId: String) {
        _walletState.update {
            it.copy(
                isConnected = true,
                accountId = accountId
            )
        }
        
        // Fetch account balance after connecting
        queryAccount(accountId)
    }

    /**
     * Disconnect wallet
     */
    fun disconnectWallet() {
        _walletState.update {
            WalletState()
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Set selected endpoint
     */
    fun setSelectedEndpoint(endpoint: RpcEndpoint) {
        _uiState.update { it.copy(selectedEndpoint = endpoint) }
    }

    override fun onCleared() {
        super.onCleared()
        repository.close()
    }
}

/**
 * UI State data class
 */
data class NearUiState(
    val isLoading: Boolean = false,
    val error: NearError? = null,
    val lastEndpoint: String? = null,
    val selectedEndpoint: RpcEndpoint = RpcEndpoint.NetworkInfo
)