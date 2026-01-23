package com.nextime.orchestrator.domain.services

import com.nextime.orchestrator.domain.Event
import com.nextime.orchestrator.domain.SagaHandler
import com.nextime.orchestrator.domain.enums.EEventSource
import com.nextime.orchestrator.domain.enums.EQueues
import com.nextime.orchestrator.domain.enums.ESagaStatus
import com.nextime.orchestrator.application.exception.InvalidSagaEventException
import com.nextime.orchestrator.domain.exception.SagaStepNotFoundException
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class SagaExecutionService {

    fun getNextQueue(event: Event): EQueues {
        if (event.source == null || event.status == null) {
            throw InvalidSagaEventException()
        }

        return getAllNextQueues(event)
            .firstOrNull()
            ?: throw SagaStepNotFoundException(event.source!!, event.status!!)
    }

    fun getAllNextQueues(event: Event): List<EQueues> {
        if (event.source == null || event.status == null) {
            throw InvalidSagaEventException()
        }

        return SagaHandler.SAGA_HANDLER
            .filter { isEventSourceAndStatusValid(event, it) }
            .map { it.third }
    }

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

