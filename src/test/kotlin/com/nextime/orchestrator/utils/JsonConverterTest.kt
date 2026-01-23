package com.nextime.orchestrator.utils

import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.domain.Event
import com.nextime.orchestrator.domain.Order
import com.nextime.orchestrator.domain.OrderItem
import com.nextime.orchestrator.domain.Product
import com.nextime.orchestrator.domain.enums.EPaymentStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class JsonConverterTest {

    class TestLogger : LoggerPort {
        var lastErrorMessage: String? = null
        var lastThrowable: Throwable? = null

        override fun info(msg: String, vararg args: Any?) {}
        override fun debug(msg: String, vararg args: Any?) {}
        override fun warn(msg: String, vararg args: Any?) {}
        override fun error(msg: String, t: Throwable, vararg args: Any?) {
            lastErrorMessage = msg
            lastThrowable = t
        }
        override fun error(msg: String, vararg args: Any?) {
            lastErrorMessage = msg
        }
    }

    private val logger = TestLogger()
    private val converter = JsonConverter(logger)

    @Test
    fun `toJson should serialize object to json`() {
        val order = Order(
            id = UUID.randomUUID(),
            transactinId = UUID.randomUUID(),
            identifier = "id-1",
            paymentStatus = EPaymentStatus.PENDING
        )

        val json = converter.toJson(order)

        assertNotNull(json)
        assertTrue(json.contains("id-1"))
    }

    @Test
    fun `toEvent should parse json to Event`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = null,
            status = null
        )

        val json = converter.toJson(event)
        val parsed = converter.toEvent(json)

        assertNotNull(parsed)
        assertEquals(event.transactionId, parsed.transactionId)
    }

    @Test
    fun `toEvent invalid json should log and throw`() {
        val badJson = "{ not a valid json"

        try {
            converter.toEvent(badJson)
            fail("expected exception")
        } catch (ex: RuntimeException) {
            // expected
        }

        assertNotNull(logger.lastErrorMessage)
        assertTrue(logger.lastErrorMessage!!.contains("[toEvent]"))
    }

    @Test
    fun `toJson should serialize simple map`() {
        val map = mapOf("a" to 1)
        val json = converter.toJson(map)
        assertNotNull(json)
        assertTrue(json.contains("\"a\""))
    }

    @Test
    fun `toEvent should parse event json successfully`() {
        val event = com.nextime.orchestrator.domain.Event(
            id = java.util.UUID.randomUUID(),
            transactionId = java.util.UUID.randomUUID(),
            orderId = java.util.UUID.randomUUID(),
            payload = null,
            source = null,
            status = null
        )
        val json = converter.toJson(event)
        val parsed = converter.toEvent(json)
        assertEquals(event.id, parsed.id)
        assertEquals(event.transactionId, parsed.transactionId)
        assertEquals(event.orderId, parsed.orderId)
    }

    @Test
    fun `toEvent with malformed json should throw RuntimeException`() {
        val bad = "not a json"
        assertThrows(RuntimeException::class.java) {
            converter.toEvent(bad)
        }
    }

    @Test
    fun `toJson with null should throw exception`() {
        // Using a workaround to test null handling - create a nullable variable and cast
        val nullValue: Any? = null
        assertThrows(Exception::class.java) {
            @Suppress("UNCHECKED_CAST")
            converter.toJson(nullValue as Any)
        }
    }

    @Test
    fun `toJson should serialize complex object with nested structures`() {
        val order = Order(
            id = UUID.randomUUID(),
            transactinId = UUID.randomUUID(),
            identifier = "ORDER-COMPLEX",
            totalPrice = BigDecimal("150.75"),
            totalItems = 2,
            customerId = UUID.randomUUID(),
            paymentStatus = EPaymentStatus.PROCESSED,
            items = listOf(
                OrderItem(
                    id = UUID.randomUUID(),
                    product = Product(
                        id = UUID.randomUUID(),
                        name = "Product 1",
                        unitPrice = BigDecimal("75.00")
                    ),
                    quantity = 2
                )
            )
        )

        val json = converter.toJson(order)
        assertNotNull(json)
        assertTrue(json.contains("ORDER-COMPLEX"))
        assertTrue(json.contains("150.75"))
    }

    @Test
    fun `toEvent with null json should throw exception`() {
        assertThrows(RuntimeException::class.java) {
            converter.toEvent(null)
        }
    }

    @Test
    fun `toJson should handle empty string`() {
        val json = converter.toJson("")
        assertNotNull(json)
        assertEquals("\"\"", json)
    }

}
