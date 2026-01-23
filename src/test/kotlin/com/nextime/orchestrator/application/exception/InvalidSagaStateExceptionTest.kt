package com.nextime.orchestrator.application.exception

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class InvalidSagaStateExceptionTest {

    @Test
    fun `InvalidSagaStateException should have correct message`() {
        val message = "Invalid saga state"
        val exception = InvalidSagaStateException(message)

        assertEquals(message, exception.message)
    }

    @Test
    fun `InvalidSagaStateException should be instance of RuntimeException`() {
        val exception = InvalidSagaStateException("Test")

        assertTrue(exception is RuntimeException)
    }
}

