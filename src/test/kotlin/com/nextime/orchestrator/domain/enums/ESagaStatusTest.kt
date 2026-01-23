package com.nextime.orchestrator.domain.enums

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ESagaStatusTest {

    @Test
    fun `all saga status should match expected values`() {
        assertEquals("SUCCESS", ESagaStatus.SUCCESS.status)
        assertEquals("ROLLBACK_PENDING", ESagaStatus.ROLLBACK_PENDING.status)
        assertEquals("FAIL", ESagaStatus.FAIL.status)
    }

    @Test
    fun `all enum values should be accessible`() {
        val allStatus = ESagaStatus.values()

        assertEquals(3, allStatus.size)
        assertTrue(allStatus.contains(ESagaStatus.SUCCESS))
        assertTrue(allStatus.contains(ESagaStatus.ROLLBACK_PENDING))
        assertTrue(allStatus.contains(ESagaStatus.FAIL))
    }
}

