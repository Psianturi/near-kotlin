package com.psianturi.near_kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.psianturi.near_kotlin.ui.screens.MainScreen
import com.psianturi.near_kotlin.viewmodel.NearViewModel

/**
 * Main Activity with improved ViewModel integration
 */
class MainActivity : ComponentActivity() {

    private val viewModel: NearViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle wallet callback from deep link
        var accountId: String? = null
        if (intent?.data != null) {
            accountId = handleWalletCallback(intent.data!!)
        }

        setContent {
            MaterialTheme {
                MainScreen(
                    viewModel = viewModel,
                    initialAccountId = accountId
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.data != null) {
            val accountId = handleWalletCallback(intent.data!!)
            accountId?.let {
                viewModel.connectWallet(it)
            }
        }
    }

    private fun handleWalletCallback(uri: Uri): String? {
        val accountId = uri.getQueryParameter("account_id")
        val publicKey = uri.getQueryParameter("public_key")
        val allKeys = uri.getQueryParameter("all_keys")
        val error = uri.getQueryParameter("error")

        if (error != null) {
            println("Wallet login failed: $error")
            return null
        } else if (accountId != null) {
            println("Wallet login success: $accountId")
            println("Public Key: $publicKey")
            println("All Keys: $allKeys")
            return accountId
        }
        return null
    }
}