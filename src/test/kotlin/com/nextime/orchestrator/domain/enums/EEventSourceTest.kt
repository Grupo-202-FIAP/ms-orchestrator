package com.nextime.orchestrator.domain.enums

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EEventSourceTest {

    @Test
    fun `all event sources should match expected values`() {
        assertEquals("ORCHESTRATOR", EEventSource.ORCHESTRATOR.source)
        assertEquals("PAYMENT", EEventSource.PAYMENT.source)
        assertEquals("PRODUCTION", EEventSource.PRODUCTION.source)
    }

    @Test
    fun `all enum values should be accessible`() {
        val allSources = EEventSource.values()

        assertEquals(3, allSources.size)
        assertTrue(allSources.contains(EEventSource.ORCHESTRATOR))
        assertTrue(allSources.contains(EEventSource.PAYMENT))
        assertTrue(allSources.contains(EEventSource.PRODUCTION))
    }
}

