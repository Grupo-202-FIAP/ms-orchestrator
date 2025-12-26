package com.nextime.orchestrator.domain.services

import com.nextime.orchestrator.domain.Event
import com.nextime.orchestrator.domain.SagaHandler
import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.EQueues
import com.nextime.orchestrator.domain.enums.ESagaStatus
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

/**
 * Serviço de domínio responsável por determinar o próximo passo da saga
 * baseado no SagaHandler. Não possui dependências externas.
 */
@Service
class SagaExecutionService {

    fun getNextQueue(event: Event): EQueues {
        if (event.source == null || event.status == null) {
            throw IllegalArgumentException("Event source ou status não pode ser null")
        }

        return getAllNextQueues(event)
            .firstOrNull()
            ?: throw IllegalArgumentException("Nenhum tópico encontrado para o source ${event.source} e status ${event.status}")
    }

    /**
     * Retorna todas as filas para um source/status (útil para fan-out)
     * Por exemplo: ORCHESTRATOR + SUCCESS retorna [PRODUCTION_QUEUE, PAYMENT_QUEUE]
     */
    fun getAllNextQueues(event: Event): List<EQueues> {
        if (event.source == null || event.status == null) {
            throw IllegalArgumentException("Event source ou status não pode ser null")
        }

        return SagaHandler.SAGA_HANDLER
            .filter { isEventSourceAndStatusValid(event, it) }
            .map { it.third }
    }

    /**
     * Verifica se a saga terminou baseado no SagaHandler
     * A saga termina quando o próximo passo leva para ORDER_CALLBACK_QUEUE
     */
    fun isSagaFinished(event: Event): Boolean {
        if (event.source == null || event.status == null) {
            return false
        }

        val nextQueues = getAllNextQueues(event)
        return nextQueues.contains(EQueues.ORDER_CALLBACK_QUEUE)
    }

    private fun isEventSourceAndStatusValid(event: Event, row: Triple<EEventSource, ESagaStatus, EQueues>): Boolean {
        val source = row.first
        val status = row.second
        return event.source == source && event.status == status
    }
}

