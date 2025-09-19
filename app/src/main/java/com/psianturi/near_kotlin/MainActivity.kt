package com.psianturi.near_kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {

    private val repo = NearRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var accountId: String? = null
        if (intent?.data != null) {
            accountId = handleWalletCallback(intent.data!!)
        }

        setContent {
            MaterialTheme {
                AppScreen(repo, accountId)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.data != null) {
            val accountId = handleWalletCallback(intent.data!!)
            setContent {
                MaterialTheme {
                    AppScreen(repo, accountId)
                }
            }
        }
    }

    private fun handleWalletCallback(uri: Uri): String? {
        val accountId = uri.getQueryParameter("account_id")
        val error = uri.getQueryParameter("error")

        if (error != null) {
            println("Wallet login failed: $error")
            return null
        } else if (accountId != null) {
            println("Wallet login success: $accountId")
            return accountId
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        repo.close()
    }
}