package com.nextime.orchestrator.utils

import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.domain.Event
import com.nextime.orchestrator.domain.Order
import com.nextime.orchestrator.domain.enums.EPaymentStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
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

}
