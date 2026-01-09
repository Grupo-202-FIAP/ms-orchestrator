package com.nextime.orchestrator.infrastructure.messaging.consumer

import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.application.ports.`in`.OrchestrationUseCase
import com.nextime.orchestrator.infrastructure.exception.MessageConsumeException
import com.nextime.orchestrator.utils.JsonConverter
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class SagaOrchestratorConsumerTest {

    class TestLogger : LoggerPort {
        override fun info(msg: String, vararg args: Any?) {}
        override fun debug(msg: String, vararg args: Any?) {}
        override fun warn(msg: String, vararg args: Any?) {}
        override fun error(msg: String, t: Throwable?, vararg args: Any?) {}
        override fun error(msg: String, vararg args: Any?) {}
    }

    @Test
    fun `handleCallbackEvent should call orchestration usecase`() {
        val logger = TestLogger()
        val usecase = Mockito.mock(OrchestrationUseCase::class.java)
        val jsonConverter = Mockito.mock(JsonConverter::class.java)

        val event = com.nextime.orchestrator.domain.Event(
            id = java.util.UUID.randomUUID(),
            transactionId = java.util.UUID.randomUUID(),
            orderId = java.util.UUID.randomUUID(),
            payload = null,
            source = null,
            status = null
        )

        Mockito.`when`(jsonConverter.toEvent(Mockito.any(String::class.java))).thenReturn(event)

        val consumer = SagaOrchestratorConsumer(logger, usecase, jsonConverter)

        consumer.consumePaymentCallbackQueue("{}")

        Mockito.verify(usecase).handleSaga(event)
    }

    @Test
    fun `handleCallbackEvent when converter throws should wrap in MessageConsumeException`() {
        val logger = TestLogger()
        val usecase = Mockito.mock(OrchestrationUseCase::class.java)
        val jsonConverter = Mockito.mock(JsonConverter::class.java)

        Mockito.`when`(jsonConverter.toEvent(Mockito.any(String::class.java))).thenThrow(RuntimeException("boom"))

        val consumer = SagaOrchestratorConsumer(logger, usecase, jsonConverter)

        assertThrows(MessageConsumeException::class.java) {
            consumer.consumeProductionCallbackQueue("{}")
        }
    }
}





