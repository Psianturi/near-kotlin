// File: app/src/main/java/com/psianturi/near_kotlin/NearRepository.kt
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
import androidx.core.net.toUri
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

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
        rpcUrl = "https://rpc.testnet.near.org" // Hapus spasi di akhir
    )

    private val client = NearRpcClient(transport)

    // Karena sebagian besar metode masih mengembalikan JsonElement,
    // kita tetap menggunakan tipe kembalian JsonElement untuk sekarang.
    suspend fun getStatus(): JsonElement? = withContext(Dispatchers.IO) {
        try {
            client.status()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

//    suspend fun getBlock(): JsonElement? = withContext(Dispatchers.IO) {
//        try {
//            // Contoh memanggil block dengan parameter
//            val params = kotlinx.serialization.json.buildJsonObject {
//                put("finality", "final")
//            }
//            client.block(params)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }

    suspend fun getBlock(): JsonElement? = withContext(Dispatchers.IO) {
        try {
            // Bangun parameter JSON untuk metode "block"
            val blockParams: JsonObject = buildJsonObject {
                put("finality", JsonPrimitive("final")) // <- PERBAIKAN: Gunakan JsonPrimitive
            }
            // Panggil metode RPC "block" secara langsung dengan parameter
            // Ini adalah cara paling andal jika signature client.block() bermasalah
            transport.call<JsonObject, JsonElement>("block", blockParams)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getGasPrice(): JsonElement? = withContext(Dispatchers.IO) {
        try {
            client.gasPrice()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getNetworkInfo(): JsonElement? = withContext(Dispatchers.IO) {
        try {
            client.networkInfo()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Tambahkan metode lain sesuai kebutuhan...

    fun getWalletLoginUrl(): Uri {
        val successUrl = "myapp://callback"
        val failureUrl = "myapp://callback?error=true"

        val url = "https://wallet.testnet.near.org/login/".toUri().buildUpon()
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