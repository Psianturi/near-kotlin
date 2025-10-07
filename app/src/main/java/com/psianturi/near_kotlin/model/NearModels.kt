package com.psianturi.near_kotlin.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Data models for NEAR blockchain data
 */

@Serializable
data class NetworkStatus(
    val chainId: String? = null,
    val latestProtocolVersion: Int? = null,
    val syncInfo: SyncInfo? = null,
    val validatorAccountId: String? = null
)

@Serializable
data class SyncInfo(
    val latestBlockHash: String? = null,
    val latestBlockHeight: Long? = null,
    val latestBlockTime: String? = null,
    val syncing: Boolean? = null
)

@Serializable
data class BlockInfo(
    val blockHeight: Long? = null,
    val blockHash: String? = null,
    val timestamp: Long? = null,
    val totalSupply: String? = null
)

@Serializable
data class ValidatorInfo(
    val accountId: String,
    val stake: String? = null,
    val isSlashed: Boolean? = false
)

@Serializable
data class GasPriceInfo(
    val gasPrice: String? = null
)

@Serializable
data class AccountInfo(
    val accountId: String,
    val amount: String? = null,
    val locked: String? = null,
    val codeHash: String? = null,
    val storageUsage: Long? = null
)

@Serializable
data class TransactionStatus(
    val status: String? = null,
    val transactionHash: String? = null,
    val receipts: List<JsonElement>? = null
)

@Serializable
data class ContractCallParams(
    val accountId: String,
    val methodName: String,
    val args: String = "{}",
    val finality: String = "final"
)

@Serializable
data class QueryRequest(
    val requestType: String,
    val finality: String = "final",
    val accountId: String? = null,
    val methodName: String? = null,
    val argsBase64: String? = null
)

/**
 * UI State models
 */
data class WalletState(
    val isConnected: Boolean = false,
    val accountId: String? = null,
    val balance: String? = null
)

sealed class RpcEndpoint(val name: String, val displayName: String) {
    data object NetworkInfo : RpcEndpoint("network_info", "Network Info")
    data object Status : RpcEndpoint("status", "Status")
    data object Block : RpcEndpoint("block", "Block")
    data object GasPrice : RpcEndpoint("gas_price", "Gas Price")
    data object Validators : RpcEndpoint("validators", "Validators")
    data object Health : RpcEndpoint("health", "Health Check")
    data object ProtocolConfig : RpcEndpoint("EXPERIMENTAL_protocol_config", "Protocol Config")
    data object GenesisConfig : RpcEndpoint("genesis_config", "Genesis Config")
    data object Chunk : RpcEndpoint("chunk", "Chunk Details")
    data object Changes : RpcEndpoint("changes", "State Changes")
    
    companion object {
        fun all() = listOf(
            NetworkInfo, Status, Block, GasPrice, Validators,
            Health, ProtocolConfig, GenesisConfig, Chunk, Changes
        )
    }
}