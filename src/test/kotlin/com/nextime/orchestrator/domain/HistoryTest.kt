package com.nextime.orchestrator.domain

import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.ESagaStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class HistoryTest {

    @Test
    fun `history should have all fields accessible`() {
        val history = History(
            source = EEventSource.PAYMENT,
            status = ESagaStatus.SUCCESS,
            message = "Payment processed",
            createdAt = LocalDateTime.now()
        )

        assertEquals(EEventSource.PAYMENT, history.source)
        assertEquals(ESagaStatus.SUCCESS, history.status)
        assertEquals("Payment processed", history.message)
        assertNotNull(history.createdAt)
    }

    @Test
    fun `history with null message should work`() {
        val history = History(
            source = EEventSource.PRODUCTION,
            status = ESagaStatus.FAIL,
            message = null
        )

        assertNull(history.message)
        assertNotNull(history.createdAt)
    }

    @Test
    fun `history should default createdAt when not provided`() {
        val history = History(
            source = EEventSource.ORCHESTRATOR,
            status = ESagaStatus.SUCCESS,
            message = "Test"
        )

        assertNotNull(history.createdAt)
    }

    @Test
    fun `history copy should create new instance with modified fields`() {
        val original = History(
            source = EEventSource.PAYMENT,
            status = ESagaStatus.SUCCESS,
            message = "Original message"
        )

        val copied = original.copy(
            status = ESagaStatus.FAIL,
            message = "Updated message"
        )

        assertEquals(ESagaStatus.SUCCESS, original.status)
        assertEquals("Original message", original.message)
        assertEquals(ESagaStatus.FAIL, copied.status)
        assertEquals("Updated message", copied.message)
        assertEquals(original.source, copied.source)
    }
}

