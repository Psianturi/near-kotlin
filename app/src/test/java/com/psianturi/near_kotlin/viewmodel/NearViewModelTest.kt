package com.psianturi.near_kotlin.viewmodel

import com.psianturi.near_kotlin.model.*
import com.psianturi.near_kotlin.repository.NearRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.JsonPrimitive
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for NearViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NearViewModelTest {

    private lateinit var viewModel: NearViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Note: In real tests, we would mock the repository
        // For now, this demonstrates the test structure
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be correct`() = runTest {
        viewModel = NearViewModel()
        
        val initialState = viewModel.uiState.first()
        assertFalse(initialState.isLoading)
        assertNull(initialState.error)
        assertEquals(RpcEndpoint.NetworkInfo, initialState.selectedEndpoint)
    }

    @Test
    fun `wallet connection should update wallet state`() = runTest {
        viewModel = NearViewModel()
        
        val testAccountId = "test.testnet"
        viewModel.connectWallet(testAccountId)
        
        advanceUntilIdle()
        
        val walletState = viewModel.walletState.first()
        assertTrue(walletState.isConnected)
        assertEquals(testAccountId, walletState.accountId)
    }

    @Test
    fun `disconnect wallet should reset wallet state`() = runTest {
        viewModel = NearViewModel()
        
        // First connect
        viewModel.connectWallet("test.testnet")
        advanceUntilIdle()
        
        // Then disconnect
        viewModel.disconnectWallet()
        advanceUntilIdle()
        
        val walletState = viewModel.walletState.first()
        assertFalse(walletState.isConnected)
        assertNull(walletState.accountId)
    }

    @Test
    fun `setSelectedEndpoint should update UI state`() = runTest {
        viewModel = NearViewModel()
        
        viewModel.setSelectedEndpoint(RpcEndpoint.Block)
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertEquals(RpcEndpoint.Block, uiState.selectedEndpoint)
    }

    @Test
    fun `clearError should remove error from state`() = runTest {
        viewModel = NearViewModel()
        
        viewModel.clearError()
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.first()
        assertNull(uiState.error)
    }
}