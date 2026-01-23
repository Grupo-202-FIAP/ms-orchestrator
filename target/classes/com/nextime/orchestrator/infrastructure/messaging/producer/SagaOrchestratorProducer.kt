package com.nextime.orchestrator.infrastructure.messaging.producer

import com.nextime.orchestrator.application.config.sqs.SqsConfig
import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.application.ports.out.MessageProducerPort
import com.nextime.orchestrator.infrastructure.exception.MessagePublishException
import org.springframework.stereotype.Component

@Component
class SagaOrchestratorProducer(
    private val logger: LoggerPort,
    private val sqsConfig: SqsConfig
) : MessageProducerPort {

    override fun sendEvent(payload: String?, queueUrl: String?) {
        try {
            logger.info(
                "[SagaOrchestratorProducer.sendEvent] Iniciando publicação na fila: {}",
                queueUrl
            )
            sqsConfig.sqsAsyncClientLocal()?.sendMessage { builder ->
                builder.queueUrl(queueUrl)
                builder.messageBody(payload)
            }
        } catch (ex: Exception) {
            logger.error("[SagaOrchestratorProducer.sendEvent] Falha ao publicar a mensagem an fila {}", queueUrl)
            throw MessagePublishException(queueUrl, ex)
        }
    }
}