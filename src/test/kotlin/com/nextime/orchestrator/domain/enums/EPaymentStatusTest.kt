package com.nextime.orchestrator.domain.enums

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EPaymentStatusTest {

    @Test
    fun `all payment status should match expected values`() {
        assertEquals("PROCESSED", EPaymentStatus.PROCESSED.status)
        assertEquals("PENDING", EPaymentStatus.PENDING.status)
        assertEquals("EXPIRED", EPaymentStatus.EXPIRED.status)
    }

    @Test
    fun `all enum values should be accessible`() {
        val allStatus = EPaymentStatus.values()

        assertEquals(3, allStatus.size)
        assertTrue(allStatus.contains(EPaymentStatus.PROCESSED))
        assertTrue(allStatus.contains(EPaymentStatus.PENDING))
        assertTrue(allStatus.contains(EPaymentStatus.EXPIRED))
    }
}

