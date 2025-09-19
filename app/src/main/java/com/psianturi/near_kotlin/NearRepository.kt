package com.psianturi.near_kotlin

import android.net.Uri
import com.near.jsonrpc.JsonRpcTransport
import com.near.jsonrpc.client.NearRpcClient
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

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

    private val client = NearRpcClient(transport)

    suspend fun getNetworkInfo(): String = withContext(Dispatchers.IO) {
        try {
            val response = client.networkInfo()
            response.toString()
        } catch (e: Exception) {
            "Error fetching network info: ${e.message}"
        }
    }

    suspend fun getStatus(): String = withContext(Dispatchers.IO) {
        try {
            val response = client.status()
            response.toString()
        } catch (e: Exception) {
            "Error fetching status: ${e.message}"
        }
    }

    suspend fun getBlock(): String = withContext(Dispatchers.IO) {
        try {
            val response = client.block()
            response.toString()
        } catch (e: Exception) {
            "Error fetching block: ${e.message}"
        }
    }

    suspend fun getGasPrice(): String = withContext(Dispatchers.IO) {
        try {
            val response = client.gasPrice()
            response.toString()
        } catch (e: Exception) {
            "Error fetching gas price: ${e.message}"
        }
    }

    fun getWalletLoginUrl(): Uri {
        val successUrl = "myapp://callback"
        val failureUrl = "myapp://callback?error=true"

        val url = Uri.parse("https://wallet.testnet.near.org/login/").buildUpon()
            .appendQueryParameter("success_url", successUrl)
            .appendQueryParameter("failure_url", failureUrl)
            .appendQueryParameter("contract_id", "example-contract.testnet")
            .build()

        return url
    }

    fun close() {
        httpClient.close()
    }
}