package com.nextime.orchestrator.domain.exception

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SagaConfigurationExceptionTest {

    @Test
    fun `SagaConfigurationException should have correct message`() {
        val message = "Configuration error"
        val exception = SagaConfigurationException(message)

        assertEquals(message, exception.message)
    }

    @Test
    fun `SagaConfigurationException should be instance of RuntimeException`() {
        val exception = SagaConfigurationException("Test")

        assertTrue(exception is RuntimeException)
    }
}

