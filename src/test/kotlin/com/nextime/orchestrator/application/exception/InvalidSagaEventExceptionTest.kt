package com.nextime.orchestrator.application.exception

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class InvalidSagaEventExceptionTest {

    @Test
    fun `InvalidSagaEventException with default message should have correct message`() {
        val exception = InvalidSagaEventException()

        assertNotNull(exception.message)
        assertEquals("O source e o status do evento não podem ser nulos", exception.message)
    }

    @Test
    fun `InvalidSagaEventException with custom message should have custom message`() {
        val customMessage = "Custom error message"
        val exception = InvalidSagaEventException(customMessage)

        assertEquals(customMessage, exception.message)
    }

    @Test
    fun `InvalidSagaEventException should be instance of RuntimeException`() {
        val exception = InvalidSagaEventException()

        assertTrue(exception is RuntimeException)
    }
}

