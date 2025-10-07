package com.psianturi.near_kotlin.ui.screens

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.psianturi.near_kotlin.model.NearResult
import com.psianturi.near_kotlin.model.RpcEndpoint
import com.psianturi.near_kotlin.viewmodel.NearViewModel
import kotlinx.serialization.json.JsonElement

/**
 * Main screen with improved state management using ViewModel
 */
@Composable
fun MainScreen(
    viewModel: NearViewModel,
    initialAccountId: String? = null
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val walletState by viewModel.walletState.collectAsStateWithLifecycle()
    val rpcResult by viewModel.rpcResult.collectAsStateWithLifecycle()

    // Connect wallet if account ID provided
    LaunchedEffect(initialAccountId) {
        initialAccountId?.let {
            viewModel.connectWallet(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            "NEAR Android Demo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Wallet Connection Section
        WalletSection(
            walletState = walletState,
            onConnectWallet = {
                val uri = viewModel.getWalletLoginUrl()
                openCustomTab(context, uri)
            },
            onDisconnectWallet = { viewModel.disconnectWallet() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // RPC Endpoint Section
        RpcEndpointSection(
            selectedEndpoint = uiState.selectedEndpoint,
            isLoading = uiState.isLoading,
            onEndpointSelected = { viewModel.setSelectedEndpoint(it) },
            onFetchData = { viewModel.fetchEndpointData(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Additional Actions
        AdditionalActionsSection(
            viewModel = viewModel,
            accountId = walletState.accountId
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Results Section
        ResultsSection(
            rpcResult = rpcResult,
            isLoading = uiState.isLoading,
            error = uiState.error,
            lastEndpoint = uiState.lastEndpoint
        )
    }
}

@Composable
private fun WalletSection(
    walletState: com.psianturi.near_kotlin.model.WalletState,
    onConnectWallet: () -> Unit,
    onDisconnectWallet: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Wallet Connection",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (!walletState.isConnected) {
                Button(
                    onClick = onConnectWallet,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Connected",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            walletState.accountId ?: "Unknown",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onDisconnectWallet,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Disconnect")
                    }
                }
            }
        }
    }
}

@Composable
private fun RpcEndpointSection(
    selectedEndpoint: RpcEndpoint,
    isLoading: Boolean,
    onEndpointSelected: (RpcEndpoint) -> Unit,
    onFetchData: (RpcEndpoint) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "RPC Endpoints",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Show first 5 endpoints as primary options
            RpcEndpoint.all().take(5).forEach { endpoint ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedEndpoint == endpoint,
                        onClick = { onEndpointSelected(endpoint) }
                    )
                    Text(
                        endpoint.displayName,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onFetchData(selectedEndpoint) },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Fetch ${selectedEndpoint.displayName}")
                }
            }
        }
    }
}

@Composable
private fun AdditionalActionsSection(
    viewModel: NearViewModel,
    accountId: String?
) {
    var showAccountQuery by remember { mutableStateOf(false) }
    var showContractCall by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Additional Actions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Account Query Button
            OutlinedButton(
                onClick = { showAccountQuery = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text("Query Account")
            }

            // Contract Call Button
            OutlinedButton(
                onClick = { showContractCall = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text("Call Contract View Method")
            }
        }
    }

    // Account Query Dialog
    if (showAccountQuery) {
        AccountQueryDialog(
            initialAccountId = accountId ?: "",
            onDismiss = { showAccountQuery = false },
            onQuery = { accountIdInput ->
                viewModel.queryAccount(accountIdInput)
                showAccountQuery = false
            }
        )
    }

    // Contract Call Dialog
    if (showContractCall) {
        ContractCallDialog(
            onDismiss = { showContractCall = false },
            onCall = { contractId, methodName, args ->
                viewModel.callViewMethod(contractId, methodName, args)
                showContractCall = false
            }
        )
    }
}

@Composable
private fun ResultsSection(
    rpcResult: NearResult<JsonElement>,
    isLoading: Boolean,
    error: com.psianturi.near_kotlin.model.NearError?,
    lastEndpoint: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Results",
                    style = MaterialTheme.typography.titleMedium
                )
                if (lastEndpoint != null) {
                    Text(
                        lastEndpoint,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Text(
                        text = error.toDisplayMessage(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                rpcResult is NearResult.Success -> {
                    Text(
                        text = rpcResult.data.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                else -> {
                    Text(
                        text = "Click 'Fetch' to retrieve data",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountQueryDialog(
    initialAccountId: String,
    onDismiss: () -> Unit,
    onQuery: (String) -> Unit
) {
    var accountId by remember { mutableStateOf(initialAccountId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Query Account") },
        text = {
            Column {
                Text("Enter NEAR account ID:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = accountId,
                    onValueChange = { accountId = it },
                    label = { Text("Account ID") },
                    placeholder = { Text("example.testnet") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onQuery(accountId) },
                enabled = accountId.isNotBlank()
            ) {
                Text("Query")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ContractCallDialog(
    onDismiss: () -> Unit,
    onCall: (String, String, String) -> Unit
) {
    var contractId by remember { mutableStateOf("") }
    var methodName by remember { mutableStateOf("") }
    var args by remember { mutableStateOf("{}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Call Contract View Method") },
        text = {
            Column {
                OutlinedTextField(
                    value = contractId,
                    onValueChange = { contractId = it },
                    label = { Text("Contract ID") },
                    placeholder = { Text("example.testnet") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = methodName,
                    onValueChange = { methodName = it },
                    label = { Text("Method Name") },
                    placeholder = { Text("get_message") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = args,
                    onValueChange = { args = it },
                    label = { Text("Arguments (JSON)") },
                    placeholder = { Text("{}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCall(contractId, methodName, args) },
                enabled = contractId.isNotBlank() && methodName.isNotBlank()
            ) {
                Text("Call")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun openCustomTab(context: Context, uri: Uri) {
    val intent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
    intent.launchUrl(context, uri)
}