package com.nextime.orchestrator.application.usecases

import com.nextime.orchestrator.domain.exception.SagaConfigurationException
import com.nextime.orchestrator.application.exception.SagaProcessingException
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
class OrchestrationUseCaseImpl(
    private val logger: LoggerPort,
    private val jsonConverter: JsonConverter,
    private val sagaExecutionService: SagaExecutionService,
    private val messageProducer: MessageProducerPort,
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

                else -> logger.warn("[OrchestrationService.handleSaga] evento não mapeado no fluxo: $event")
            }
        } catch (ex: Exception) {
            logger.error("[OrchestrationService.handleSaga] Erro ao processar saga: ${jsonConverter.toJson(event)}", ex)
            throw SagaProcessingException(
                "Erro ao processar saga para o evento para eventId=${event.id}",
                ex
            )
        }
    }

    override fun startSaga(event: Event) {
        event.source = EEventSource.ORCHESTRATOR
        event.status = ESagaStatus.SUCCESS
        logger.info("[OrchestrationService.startSaga] Saga iniciada com o id: ${event.id}")
        addHistory(event, "Saga iniciada!")

        val queues = sagaExecutionService.getAllNextQueues(event)

        if (queues.isEmpty()) {
            throw SagaConfigurationException(
                "No queues configured for ORCHESTRATOR + SUCCESS"
            )
        }

        logger.info("[OrchestrationService.startSaga] Enviando evento para: ${queues.size} filas producer: $queues")

        queues.forEach { queue ->
            sendToProducerWithQueue(event, queue)
        }
    }

    fun finishSagaSuccess(event: Event) {
        event.source = EEventSource.ORCHESTRATOR
        event.status = ESagaStatus.SUCCESS
        logger.info("[OrchestrationService.finishSagaSuccess] Saga finalizada com sucesso para o evento do id: ${event.id}!")
        addHistory(event, "Saga finalizada com sucesso!")
        notifyFinishedSaga(event)
    }

    fun finishSagaFail(event: Event) {
        event.source = EEventSource.ORCHESTRATOR
        event.status = ESagaStatus.FAIL
        logger.info("[OrchestrationService.finishSagaFail] Saga finalizada com erro para o evento com id: ${event.id}!")
        addHistory(event, "Saga finalizada com erros")
        notifyFinishedSaga(event)
    }

    fun handleRollback(event: Event) {
        event.source = EEventSource.ORCHESTRATOR
        logger.info("[OrchestrationService.handleRollback] Rollback da saga para o evento com o id: ${event.id}!")
        addHistory(event, "Inicializando rollback da saga")

        val queues = sagaExecutionService.getAllNextQueues(event)
        logger.info("[OrchestrationService.handleRollback] Enviando evento de rollback para as filas: $queues")

        queues.forEach { queue ->
            sendToProducerWithQueue(event, queue)
        }
    }

    fun continueSaga(event: Event) {
        val queue = sagaExecutionService.getNextQueue(event)
        logger.info("[OrchestrationService.continueSaga] Continuando saga para o evento com o id: ${event.id}")
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

