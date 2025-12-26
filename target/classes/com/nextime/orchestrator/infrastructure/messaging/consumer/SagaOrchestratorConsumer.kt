package com.nextime.orchestrator.infrastructure.messaging.consumer

import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.application.ports.`in`.OrchestrationUseCase
import com.nextime.orchestrator.utils.JsonConverter
import io.awspring.cloud.sqs.annotation.SqsListener
import org.springframework.stereotype.Component

@Component
class SagaOrchestratorConsumer(
    private val logger: LoggerPort,
    private val orchestrationUseCase: OrchestrationUseCase,
    private val jsonConverter: JsonConverter
) {

    @SqsListener("\${spring.sqs.queues.order-queue}")
    fun consumeStartSagaEvent(payload: String) {
        logger.info("Receiving event {} from start-saga topic", payload)
        val event = jsonConverter.toEvent(payload)
        orchestrationUseCase.startSaga(event)
    }

    @SqsListener("\${spring.sqs.queues.production-callback-queue}")
    fun consumeProductionCallbackQueue(payload: String) {
        logger.info("Receiving event {} from production-callback-queue", payload)
        handleCallbackEvent(payload)
    }

    @SqsListener("\${spring.sqs.queues.payment-callback-queue}")
    fun consumePaymentCallbackQueue(payload: String) {
        logger.info("Receiving event {} from payment-callback-queue", payload)
        handleCallbackEvent(payload)
    }

    private fun handleCallbackEvent(payload: String) {
        try {
            val event = jsonConverter.toEvent(payload)
            orchestrationUseCase.handleSaga(event)
        } catch (ex: Exception) {
            logger.error("Error processing callback event from SQS queue", ex)
            throw RuntimeException("Error processing event from SQS queue", ex)
        }
    }
}