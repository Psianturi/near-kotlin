package com.psianturi.near_kotlin

import com.near.jsonrpc.client.NearRpcClient
import com.near.jsonrpc.JsonRpcTransport
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class NearService {
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

    suspend fun getStatus() = client.status()
    suspend fun getBlock() = client.block()
    suspend fun getGasPrice() = client.gasPrice()
    suspend fun getValidators() = client.validators()

    fun close() {
        httpClient.close()
    }
}