package com.nextime.orchestrator.domain.exception

import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.ESagaStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SagaStepNotFoundExceptionTest {

    @Test
    fun `SagaStepNotFoundException should have correct message with source and status`() {
        val source = EEventSource.PAYMENT
        val status = ESagaStatus.FAIL
        val exception = SagaStepNotFoundException(source, status)

        assertNotNull(exception.message)
        assertTrue(exception.message!!.contains("PAYMENT"))
        assertTrue(exception.message!!.contains("FAIL"))
        assertTrue(exception.message!!.contains("Nenhum passo da saga encontrado"))
    }

    @Test
    fun `SagaStepNotFoundException should be instance of RuntimeException`() {
        val exception = SagaStepNotFoundException(EEventSource.PRODUCTION, ESagaStatus.SUCCESS)

        assertTrue(exception is RuntimeException)
    }

    @Test
    fun `SagaStepNotFoundException with different sources should format correctly`() {
        val exception1 = SagaStepNotFoundException(EEventSource.ORCHESTRATOR, ESagaStatus.SUCCESS)
        val exception2 = SagaStepNotFoundException(EEventSource.PAYMENT, ESagaStatus.ROLLBACK_PENDING)

        assertNotNull(exception1.message)
        assertNotNull(exception2.message)
        assertTrue(exception1.message!!.contains("ORCHESTRATOR"))
        assertTrue(exception2.message!!.contains("PAYMENT"))
    }
}

