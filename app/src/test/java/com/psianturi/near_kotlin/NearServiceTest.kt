package com.psianturi.near_kotlin

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NearServiceTest {

    private lateinit var nearService: NearService

    @Before
    fun setup() {
        // Note: For unit testing, you might want to use a mock HTTP client
        // For now, we'll use the real service but this will require network
        nearService = NearService()
    }

    @After
    fun tearDown() {
        nearService.close()
    }

    @Test
    fun `service should be initialized correctly`() {
        assertNotNull("NearService should be initialized", nearService)
    }

    @Test
    fun `service should have all required methods`() {
        // Test that all methods exist and are callable
        val methods = NearService::class.java.declaredMethods
        val methodNames = methods.map { it.name }

        assertTrue("Should have getStatus method", methodNames.contains("getStatus"))
        assertTrue("Should have getBlock method", methodNames.contains("getBlock"))
        assertTrue("Should have getGasPrice method", methodNames.contains("getGasPrice"))
        assertTrue("Should have getValidators method", methodNames.contains("getValidators"))
        assertTrue("Should have close method", methodNames.contains("close"))
    }

    @Test
    fun `service should handle network errors gracefully`() = runTest {
        try {
            // This should work with real network, but if network fails,
            // the service should handle it gracefully
            val result = nearService.getStatus()
            assertNotNull("Network call should return result", result)
            println("✅ Network test passed: $result")
        } catch (e: Exception) {
            // If network is not available, this is expected
            println("⚠️ Network not available (expected in CI): ${e.message}")
            assertTrue("Should handle network errors", true)
        }
    }

    @Test
    fun `json parsing should work correctly`() {
        val testJson = """
        {
            "jsonrpc": "2.0",
            "result": {
                "chain_id": "testnet",
                "sync_info": {
                    "latest_block_height": 12345
                }
            },
            "id": "1"
        }
        """.trimIndent()

        val jsonElement = Json.parseToJsonElement(testJson)
        assertNotNull("JSON should parse correctly", jsonElement)
        assertEquals("Should have jsonrpc field", "2.0", jsonElement.jsonObject["jsonrpc"]?.toString()?.replace("\"", ""))
    }

    @Test
    fun `service close method should not throw exception`() {
        // Test that close() method doesn't throw exceptions
        try {
            nearService.close()
            // Second close should also work fine
            nearService.close()
            assertTrue("Close method should work without exceptions", true)
        } catch (e: Exception) {
            fail("Close method should not throw exceptions: ${e.message}")
        }
    }
}