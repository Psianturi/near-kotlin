package com.psianturi.near_kotlin.repository

import android.net.Uri
import androidx.core.net.toUri
import com.near.jsonrpc.JsonRpcTransport
import com.near.jsonrpc.client.NearRpcClient
import com.psianturi.near_kotlin.model.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

/**
 * Improved NEAR Repository with comprehensive error handling and expanded RPC coverage
 */
class NearRepository {

    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                prettyPrint = true
            })
        }
    }

    private val transport = JsonRpcTransport(
        client = httpClient,
        rpcUrl = "https://rpc.testnet.near.org"
    )

    private val client = NearRpcClient(transport)
    
    /**
     * Get network status
     */
    suspend fun getStatus(): NearResult<JsonElement> = safeApiCall {
        client.status()
    }

    /**
     * Get block information
     */
    suspend fun getBlock(finality: String = "final"): NearResult<JsonElement> = safeApiCall {
        val params = buildJsonObject {
            put("finality", JsonPrimitive(finality))
        }
        transport.call<JsonObject, JsonElement>("block", params)
    }

    /**
     * Get gas price
     */
    suspend fun getGasPrice(blockId: Long? = null): NearResult<JsonElement> = safeApiCall {
        if (blockId != null) {
            val params = buildJsonObject {
                put("block_id", JsonPrimitive(blockId))
            }
            transport.call<JsonObject, JsonElement>("gas_price", params)
        } else {
            client.gasPrice()
        }
    }

    /**
     * Get network information
     */
    suspend fun getNetworkInfo(): NearResult<JsonElement> = safeApiCall {
        client.networkInfo()
    }

    /**
     * Get validators
     */
    suspend fun getValidators(blockId: String? = null): NearResult<JsonElement> = safeApiCall {
        if (blockId != null) {
            val params = buildJsonObject {
                put("block_id", JsonPrimitive(blockId))
            }
            transport.call<JsonObject, JsonElement>("validators", params)
        } else {
            client.validators()
        }
    }

    /**
     * Get health status
     */
    suspend fun getHealth(): NearResult<JsonElement> = safeApiCall {
        client.health()
    }

    /**
     * Get protocol configuration
     */
    suspend fun getProtocolConfig(finality: String = "final"): NearResult<JsonElement> = safeApiCall {
        val params = buildJsonObject {
            put("finality", JsonPrimitive(finality))
        }
        transport.call<JsonObject, JsonElement>("EXPERIMENTAL_protocol_config", params)
    }

    /**
     * Get genesis configuration
     */
    suspend fun getGenesisConfig(): NearResult<JsonElement> = safeApiCall {
        client.genesisConfig()
    }

    /**
     * Get chunk information
     */
    suspend fun getChunk(chunkHash: String): NearResult<JsonElement> = safeApiCall {
        val params = buildJsonObject {
            put("chunk_id", JsonPrimitive(chunkHash))
        }
        transport.call<JsonObject, JsonElement>("chunk", params)
    }

    /**
     * Get state changes
     */
    suspend fun getChanges(blockId: String, changeType: String = "all"): NearResult<JsonElement> = safeApiCall {
        val params = buildJsonObject {
            put("block_id", JsonPrimitive(blockId))
            put("changes_type", JsonPrimitive(changeType))
        }
        transport.call<JsonObject, JsonElement>("changes", params)
    }

    /**
     * Query account information
     */
    suspend fun queryAccount(accountId: String, finality: String = "final"): NearResult<JsonElement> = safeApiCall {
        val params = buildJsonObject {
            put("request_type", JsonPrimitive("view_account"))
            put("finality", JsonPrimitive(finality))
            put("account_id", JsonPrimitive(accountId))
        }
        transport.call<JsonObject, JsonElement>("query", params)
    }

    /**
     * Call contract view method
     */
    suspend fun callViewMethod(
        accountId: String,
        methodName: String,
        args: String = "{}",
        finality: String = "final"
    ): NearResult<JsonElement> = safeApiCall {
        val argsBase64 = java.util.Base64.getEncoder().encodeToString(args.toByteArray())
        val params = buildJsonObject {
            put("request_type", JsonPrimitive("call_function"))
            put("finality", JsonPrimitive(finality))
            put("account_id", JsonPrimitive(accountId))
            put("method_name", JsonPrimitive(methodName))
            put("args_base64", JsonPrimitive(argsBase64))
        }
        transport.call<JsonObject, JsonElement>("query", params)
    }

    /**
     * Get transaction status
     */
    suspend fun getTransactionStatus(txHash: String, accountId: String): NearResult<JsonElement> = safeApiCall {
        val params = buildJsonArray {
            add(JsonPrimitive(txHash))
            add(JsonPrimitive(accountId))
        }
        transport.call<JsonArray, JsonElement>("EXPERIMENTAL_tx_status", params)
    }

    /**
     * Get client configuration
     */
    suspend fun getClientConfig(): NearResult<JsonElement> = safeApiCall {
        client.clientConfig()
    }

    /**
     * Get light client proof
     */
    suspend fun getLightClientProof(txHash: String, senderId: String): NearResult<JsonElement> = safeApiCall {
        val params = buildJsonObject {
            put("type", JsonPrimitive("transaction"))
            put("transaction_hash", JsonPrimitive(txHash))
            put("sender_id", JsonPrimitive(senderId))
        }
        transport.call<JsonObject, JsonElement>("light_client_proof", params)
    }

    /**
     * Generate wallet login URL
     */
    fun getWalletLoginUrl(
        contractId: String = "example-contract.testnet",
        successUrl: String = "myapp://callback",
        failureUrl: String = "myapp://callback?error=true"
    ): Uri {
        return "https://wallet.testnet.near.org/login/".toUri().buildUpon()
            .appendQueryParameter("success_url", successUrl)
            .appendQueryParameter("failure_url", failureUrl)
            .appendQueryParameter("contract_id", contractId)
            .build()
    }

    /**
     * Clean up resources
     */
    fun close() {
        httpClient.close()
    }

    /**
     * Helper function for safe API calls with error handling
     */
    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): NearResult<T> = withContext(Dispatchers.IO) {
        try {
            val result = apiCall()
            NearResult.Success(result)
        } catch (e: Exception) {
            val error = when {
                e.message?.contains("network", ignoreCase = true) == true ->
                    NearError.NetworkError("Network connection failed", e)
                e.message?.contains("timeout", ignoreCase = true) == true ->
                    NearError.NetworkError("Request timeout", e)
                e.message?.contains("JSON-RPC", ignoreCase = true) == true ->
                    NearError.RpcError(-1, e.message ?: "RPC error occurred")
                else ->
                    NearError.Unknown(e.message ?: "Unknown error occurred", e)
            }
            NearResult.Error(error)
        }
    }
}