package com.nextime.orchestrator.infrastructure.exception

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MessageConsumeExceptionTest {

    @Test
    fun `MessageConsumeException should have correct message and cause`() {
        val message = "Consume error"
        val cause = RuntimeException("Root cause")
        val exception = MessageConsumeException(message, cause)

        assertEquals(message, exception.message)
        assertEquals(cause, exception.cause)
    }

    @Test
    fun `MessageConsumeException should be instance of RuntimeException`() {
        val exception = MessageConsumeException("Test", RuntimeException())

        assertNotNull(exception as? RuntimeException)
    }
}

