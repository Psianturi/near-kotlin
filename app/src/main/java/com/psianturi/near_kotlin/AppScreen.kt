package com.psianturi.near_kotlin

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun AppScreen(repo: NearRepository, initialAccountId: String?) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var info by remember { mutableStateOf("Click to fetch...") }
    var loading by remember { mutableStateOf(false) }
    var accountId by remember { mutableStateOf(initialAccountId) }
    var selectedEndpoint by remember { mutableStateOf("network_info") }

    val endpoints = listOf(
        "network_info" to "Network Info",
        "status" to "Status",
        "block" to "Block",
        "gas_price" to "Gas Price"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "NEAR Android Demo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Wallet Connection Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Wallet Connection", style = MaterialTheme.typography.titleMedium)

                if (accountId == null) {
                    Button(
                        onClick = {
                            val walletUri: Uri = repo.getWalletLoginUrl()
                            openCustomTab(context, walletUri)
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text("Connect Wallet")
                    }
                    Text(
                        "Connect to NEAR Testnet Wallet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Connected", color = MaterialTheme.colorScheme.primary)
                            Text(
                                accountId!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(onClick = { accountId = null }) {
                            Text("Disconnect")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // RPC Endpoint Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("RPC Endpoint", style = MaterialTheme.typography.titleMedium)

                // Endpoint Selector
                endpoints.forEach { (endpoint, displayName) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedEndpoint == endpoint,
                            onClick = { selectedEndpoint = endpoint }
                        )
                        Text(displayName, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fetch Button
                Button(
                    onClick = {
                        scope.launch {
                            loading = true
                            info = try {
                                val result = when (selectedEndpoint) {
                                    "network_info" -> repo.getNetworkInfo()
                                    "status" -> repo.getStatus()
                                    "block" -> repo.getBlock()
                                    "gas_price" -> repo.getGasPrice()
                                    else -> null
                                }
                                result?.toString() ?: "Unknown endpoint"
                            } catch (e: Exception) {
                                "Error: ${e.message}"
                            }
                            loading = false
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fetch ${endpoints.find { it.first == selectedEndpoint }?.second ?: "Data"}")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Results Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Results", style = MaterialTheme.typography.titleMedium)

                if (loading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Text(
                        text = info,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

private fun openCustomTab(context: Context, uri: Uri) {
    val intent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
    intent.launchUrl(context, uri)
}