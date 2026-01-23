package com.nextime.orchestrator.application.exception

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SagaProcessingExceptionTest {

    @Test
    fun `SagaProcessingException with message should have correct message`() {
        val message = "Processing error"
        val exception = SagaProcessingException(message)

        assertEquals(message, exception.message)
        assertNull(exception.cause)
    }

    @Test
    fun `SagaProcessingException with message and cause should have both`() {
        val message = "Processing error"
        val cause = RuntimeException("Root cause")
        val exception = SagaProcessingException(message, cause)

        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `SagaProcessingException should be instance of RuntimeException`() {
        val exception = SagaProcessingException("Test")

        assertNotNull(exception as? RuntimeException)
    }
}

