package com.nextime.orchestrator.domain

import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.ESagaStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class EventTest {

    @Test
    fun `addToHistory should append history to event`() {
        val event = Event(
            id = UUID.randomUUID(),
            transactionId = UUID.randomUUID(),
            orderId = UUID.randomUUID(),
            payload = null,
            source = EEventSource.ORCHESTRATOR,
            status = ESagaStatus.SUCCESS,
            eventHistory = emptyList(),
            createdAt = LocalDateTime.now()
        )

        val history = History(
            source = EEventSource.ORCHESTRATOR,
            status = ESagaStatus.SUCCESS,
            message = "test",
            createdAt = LocalDateTime.now()
        )

        val newEvent = event.addToHistory(history)

        assertNotNull(newEvent.eventHistory)
        assertEquals(1, newEvent.eventHistory.size)
        assertEquals("test", newEvent.eventHistory.first().message)
        // original event should remain immutable
        assertEquals(0, event.eventHistory.size)
    }
}







