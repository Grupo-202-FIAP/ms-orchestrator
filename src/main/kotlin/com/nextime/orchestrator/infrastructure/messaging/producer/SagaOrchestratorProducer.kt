package com.nextime.orchestrator.infrastructure.messaging.producer

import com.nextime.orchestrator.application.config.sqs.SqsConfig
import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.application.ports.out.MessageProducerPort
import org.springframework.stereotype.Component

@Component
class SagaOrchestratorProducer(
    private val logger: LoggerPort,
    private val sqsConfig: SqsConfig
) : MessageProducerPort {

    override fun sendEvent(payload: String?, queueUrl: String?) {
        try {
            logger.info("Sending event to queue {} with data {}", queueUrl, payload)
            sqsConfig.sqsAsyncClient().sendMessage { builder ->
                builder.queueUrl(queueUrl)
                builder.messageBody(payload)
            }
        } catch (ex: Exception) {
            logger.error("Error trying to send data to queue {} with data {}", queueUrl, payload, ex)
        }
    }
}