package com.nextime.orchestrator.application.usecases

import com.nextime.orchestrator.application.ports.`in`.OrchestrationUseCase
import com.nextime.orchestrator.application.gateways.LoggerPort
import com.nextime.orchestrator.application.ports.out.MessageProducerPort
import com.nextime.orchestrator.domain.Event
import com.nextime.orchestrator.domain.History
import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.EQueues
import com.nextime.orchestrator.domain.enums.ESagaStatus
import com.nextime.orchestrator.domain.services.SagaExecutionService
import com.nextime.orchestrator.utils.JsonConverter
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OrchestrationService(
    private val logger: LoggerPort,
    private val jsonConverter: JsonConverter,
    private val sagaExecutionService: SagaExecutionService,
    private val messageProducer: MessageProducerPort,
    private val queueUrlResolver: com.nextime.orchestrator.infrastructure.config.QueueUrlResolver
) : OrchestrationUseCase {

    override fun handleSaga(event: Event) {
        try {
            when {
                // Evento nunca processado → start
                event.source == null || event.status == null -> startSaga(event)

                // Evento terminou produção ou pagamento com sucesso → continue
                event.status == ESagaStatus.SUCCESS && !isSagaFinished(event) -> continueSaga(event)

                // Evento falhou → finalizar com erro
                event.status == ESagaStatus.FAIL -> finishSagaFail(event)

                // Evento terminou produção → finaliza saga
                event.status == ESagaStatus.SUCCESS && isSagaFinished(event) -> finishSagaSuccess(event)

                event.status == ESagaStatus.ROLLBACK_PENDING -> handleRollback(event)

                else -> logger.warn("Evento desconhecido ou fora do fluxo: $event")
            }
        } catch (ex: Exception) {
            logger.error("Erro ao processar saga: ${jsonConverter.toJson(event)}", ex)
            throw RuntimeException("Erro ao processar saga: ${jsonConverter.toJson(event)}", ex)
        }
    }

    override fun startSaga(event: Event) {
        event.source = EEventSource.ORCHESTRATOR
        event.status = ESagaStatus.SUCCESS
        logger.info("SAGA STARTED! Event: ${event.id}")
        addHistory(event, "Saga started!")

        val queues = sagaExecutionService.getAllNextQueues(event)

        if (queues.isEmpty()) {
            throw IllegalStateException("Nenhuma fila configurada no SagaHandler para ORCHESTRATOR + SUCCESS")
        }

        logger.info("Sending event to ${queues.size} producer queues: $queues")

        queues.forEach { queue ->
            sendToProducerWithQueue(event, queue)
        }
    }

    fun finishSagaSuccess(event: Event) {
        event.source = EEventSource.ORCHESTRATOR
        event.status = ESagaStatus.SUCCESS
        logger.info("SAGA FINISHED SUCCESSFULLY FOR EVENT ${event.id}!")
        addHistory(event, "Saga finished successfully!")
        notifyFinishedSaga(event)
    }

    fun finishSagaFail(event: Event) {
        event.source = EEventSource.ORCHESTRATOR
        event.status = ESagaStatus.FAIL
        logger.info("SAGA FINISHED WITH ERRORS FOR EVENT ${event.id}!")
        addHistory(event, "Saga finished with errors!")
        notifyFinishedSaga(event)
    }

    fun handleRollback(event: Event) {
        event.source = EEventSource.ORCHESTRATOR
        logger.info("SAGA ROLLBACK FOR EVENT ${event.id}!")
        addHistory(event, "Saga rollback initiated")

        val queue = sagaExecutionService.getNextQueue(event)
        logger.info("Sending rollback event to queue: $queue")
        sendToProducerWithQueue(event, queue)
    }

    fun continueSaga(event: Event) {
        val queue = sagaExecutionService.getNextQueue(event)
        logger.info("SAGA CONTINUING FOR EVENT ${event.id}")
        sendToProducerWithQueue(event, queue)
    }

    private fun addHistory(event: Event, message: String?) {
        val history = History(
            source = event.source!!,
            status = event.status!!,
            message = message,
            createdAt = LocalDateTime.now()
        )
        event.eventHistory += history
    }

    private fun isSagaFinished(event: Event): Boolean {
        return sagaExecutionService.isSagaFinished(event)
    }

    private fun notifyFinishedSaga(event: Event) {
        val queue = sagaExecutionService.getNextQueue(event)
        val eventJson = jsonConverter.toJson(event)
        messageProducer.sendEvent(eventJson, EQueues.ORDER_CALLBACK_QUEUE.queueName)
    }

    private fun sendToProducerWithQueue(event: Event, queue: EQueues) {
        val eventJson = jsonConverter.toJson(event)
        messageProducer.sendEvent(eventJson, queue.queueName)
    }
}

